package io.trino.plugin.telemetrycollector.connector;

import io.trino.spi.connector.ConnectorTransactionHandle;

public enum TelemetryDataTransactionHandle
        implements ConnectorTransactionHandle
{
    INSTANCE
}
