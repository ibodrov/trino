package io.trino.tracing.receiver;

public class TelemetryStoreException
        extends RuntimeException
{
    public TelemetryStoreException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
