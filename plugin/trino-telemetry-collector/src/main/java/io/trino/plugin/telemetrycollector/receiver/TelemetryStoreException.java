package io.trino.plugin.telemetrycollector.receiver;

public class TelemetryStoreException
        extends RuntimeException
{
    public TelemetryStoreException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
