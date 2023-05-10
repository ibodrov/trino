package io.trino.tracing.receiver;

import io.opentelemetry.proto.trace.v1.Span;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static com.google.common.io.MoreFiles.deleteRecursively;
import static com.google.common.io.RecursiveDeleteOption.ALLOW_INSECURE;
import static java.util.Objects.requireNonNull;
import static org.testng.AssertJUnit.assertEquals;

public class TestFilesystemTelemetryStore
{
    private Path tempDir;

    @Test
    public void testSaveAndReadSpans()
    {
        Collection<Span> exampleSpans = loadExampleSpans();

        FilesystemTelemetryStore store = new FilesystemTelemetryStore(tempDir);
        store.saveSpans(exampleSpans);

        Collection<Span> savedSpans = store.readSpans();
        assertEquals(exampleSpans.size(), savedSpans.size());
    }

    private Collection<Span> loadExampleSpans()
    {
        InputStream resource = requireNonNull(getClass().getResourceAsStream("/example_spans.jsonl"));
        return new BufferedReader(new InputStreamReader(resource))
                .lines()
                .map(JsonHelper::deserializeSpan)
                .toList();
    }

    @BeforeMethod
    public void setUp()
    {
        try {
            tempDir = Files.createTempDirectory("telemetry");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown()
    {
        if (tempDir != null) {
            try {
                deleteRecursively(tempDir, ALLOW_INSECURE);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
