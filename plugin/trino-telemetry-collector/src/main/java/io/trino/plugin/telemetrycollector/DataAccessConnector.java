package io.trino.plugin.telemetrycollector;

import com.google.common.collect.ImmutableSet;
import io.trino.spi.connector.Connector;
import io.trino.spi.connector.ConnectorMetadata;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.ptf.ConnectorTableFunction;

import javax.inject.Inject;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public class DataAccessConnector
        implements Connector
{
    private final DataAccessMetadata metadata;

    @Inject
    public DataAccessConnector(DataAccessMetadata metadata)
    {
        this.metadata = requireNonNull(metadata, "metadata is null");
    }

    @Override
    public ConnectorMetadata getMetadata(ConnectorSession session, ConnectorTransactionHandle transactionHandle)
    {
        return metadata;
    }

    @Override
    public Set<ConnectorTableFunction> getTableFunctions()
    {
        return ImmutableSet.of(new ReadSpansTableFunction());
    }
}
