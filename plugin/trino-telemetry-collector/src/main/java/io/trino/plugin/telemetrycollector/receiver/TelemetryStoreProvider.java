package io.trino.plugin.telemetrycollector.receiver;

import javax.inject.Provider;

import java.nio.file.Path;

public class TelemetryStoreProvider
        implements Provider<TelemetryStore>
{
    @Override
    public TelemetryStore get()
    {
        Path baseDir = Path.of("/tmp/telemetry"); // TODO
        return new FilesystemTelemetryStore(baseDir);
    }
}
