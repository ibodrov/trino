package io.trino.plugin.telemetrycollector.ui;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.io.IOException;

@Path("/jaeger-ui")
public class JaegerUIResource
{
    private final UIContent uiContent;

    public JaegerUIResource()
    {
        this.uiContent = new UIContent("jaeger-ui", "index.html");
    }

    /**
     * Static resources.
     */
    @GET
    @Path("{path: .*}")
    public Response serve(@PathParam("path") String path,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context ServletContext servletContext)
            throws IOException
    {
        return uiContent.getFile(path, ifNoneMatch, servletContext);
    }
}
