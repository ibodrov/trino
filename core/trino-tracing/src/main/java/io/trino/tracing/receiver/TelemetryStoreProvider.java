package io.trino.tracing.receiver;

import javax.inject.Provider;

import java.nio.file.Path;
import java.time.Instant;

public class TelemetryStoreProvider
        implements Provider<TelemetryStore>
{
    @Override
    public TelemetryStore get()
    {
        Path baseDir = Path.of("/tmp/telemetry/" + Instant.now().toString()); // TODO
        return new FilesystemTelemetryStore(baseDir);
    }
}
