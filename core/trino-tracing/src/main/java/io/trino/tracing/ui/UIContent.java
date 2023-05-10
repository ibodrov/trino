/*
 * Copyright Starburst Data, Inc. All rights reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF STARBURST DATA.
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 * Redistribution of this material is strictly prohibited.
 */
package io.trino.tracing.ui;

import javax.servlet.ServletContext;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;
import static java.util.Objects.requireNonNull;

/**
 * Copied from stargate.
 */
public class UIContent
{
    private static final String[] STATIC_RESOURCES = {
            ".css",
            ".html",
            ".ico",
            ".jpg",
            ".js",
            ".json",
            ".map",
            ".png",
            ".svg",
            ".ttf",
            ".txt",
            ".woff",
            ".woff2",
    };

    private final Map<URL, EntityTag> entityTags;
    private final String rootPath;
    private final String defaultFile;

    public UIContent(String rootPath, String defaultFile)
    {
        checkArgument(!rootPath.startsWith("/") && !rootPath.endsWith("/"), "rootPath must not start or end with '/': %s", rootPath);
        this.rootPath = "/" + requireNonNull(rootPath, "rootPath cannot be null") + "/";
        this.defaultFile = requireNonNull(defaultFile, "defaultFile cannot be null");
        verify(getClass().getResource(this.rootPath + defaultFile) != null, "UI static resources '%s' not found", this.rootPath);
        this.entityTags = new ConcurrentHashMap<>();
    }

    public Response getFile(String path, String ifNoneMatch, ServletContext servletContext)
            throws IOException
    {
        if (path.isEmpty() || !isResource(path)) {
            path = defaultFile;
        }

        String fullPath = rootPath + path;
        if (!isCanonical(fullPath)) {
            // consider redirecting to the absolute path
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        URL resource = getClass().getResource(fullPath);
        if (resource == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        EntityTag eTag = entityTags.computeIfAbsent(resource, UIContent::calculateETag);
        EntityTag requestETag = getRequestETag(ifNoneMatch);

        if (eTag.equals(requestETag)) {
            return Response.notModified(eTag)
                    .build();
        }

        CacheControl cacheControl = new CacheControl();

        return Response.ok(resource.openStream(), servletContext.getMimeType(resource.toString()))
                .cacheControl(cacheControl)
                .tag(eTag)
                .build();
    }

    private static boolean isResource(String path)
    {
        if (path.isEmpty()) {
            return false;
        }

        for (String ext : STATIC_RESOURCES) {
            if (path.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCanonical(String fullPath)
    {
        try {
            return new URI(fullPath).normalize().getPath().equals(fullPath);
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    private static EntityTag calculateETag(URL resource)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA3-256");

            byte[] buf = new byte[1024];
            try (DigestInputStream in = new DigestInputStream(resource.openStream(), md)) {
                while (true) {
                    if (in.read(buf) < 0) {
                        break;
                    }
                }
            }

            Base64.Encoder encoder = Base64.getEncoder();
            String tag = encoder.encodeToString(md.digest());
            return new EntityTag(tag);
        }
        catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static EntityTag getRequestETag(String ifNoneMatch)
    {
        if (ifNoneMatch == null) {
            return null;
        }
        return EntityTag.valueOf(ifNoneMatch);
    }
}
