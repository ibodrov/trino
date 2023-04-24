package io.trino.plugin.telemetrycollector;

import io.trino.spi.connector.ConnectorTransactionHandle;

public enum DataAccessTransactionHandle
        implements ConnectorTransactionHandle
{
    INSTANCE
}
