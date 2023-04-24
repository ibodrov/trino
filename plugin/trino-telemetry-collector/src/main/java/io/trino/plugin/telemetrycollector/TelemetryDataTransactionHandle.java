package io.trino.plugin.telemetrycollector;

import io.trino.spi.connector.ConnectorTransactionHandle;

public enum TelemetryDataTransactionHandle
        implements ConnectorTransactionHandle
{
    INSTANCE
}
