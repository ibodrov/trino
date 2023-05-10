package io.trino.tracing.plugin;

import io.trino.spi.connector.ConnectorTransactionHandle;

public enum TracingDataTransactionHandle
        implements ConnectorTransactionHandle
{
    INSTANCE
}
