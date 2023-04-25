package io.trino.plugin.telemetrycollector.connector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.trino.plugin.telemetrycollector.receiver.TelemetryStore;
import io.trino.spi.connector.ColumnHandle;
import io.trino.spi.connector.Connector;
import io.trino.spi.connector.ConnectorMetadata;
import io.trino.spi.connector.ConnectorRecordSetProvider;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorSplit;
import io.trino.spi.connector.ConnectorSplitManager;
import io.trino.spi.connector.ConnectorTableHandle;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.connector.InMemoryRecordSet;
import io.trino.spi.connector.RecordSet;
import io.trino.spi.function.FunctionProvider;
import io.trino.spi.ptf.ConnectorTableFunction;
import io.trino.spi.ptf.ConnectorTableFunctionHandle;
import io.trino.spi.ptf.TableFunctionProcessorProvider;
import io.trino.spi.ptf.TableFunctionSplitProcessor;
import io.trino.spi.transaction.IsolationLevel;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.trino.spi.type.VarcharType.VARCHAR;
import static java.util.Objects.requireNonNull;

public class TelemetryDataConnector
        implements Connector
{
    private final TelemetryDataMetadata metadata;
    private final TelemetryStore telemetryStore;

    @Inject
    public TelemetryDataConnector(TelemetryDataMetadata metadata, TelemetryStore telemetryStore)
    {
        this.metadata = requireNonNull(metadata, "metadata is null");
        this.telemetryStore = requireNonNull(telemetryStore, "telemetryStore is null");
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

    @Override
    public ConnectorSplitManager getSplitManager()
    {
        return new TelemetryDataSplitManager();
    }

    @Override
    public ConnectorRecordSetProvider getRecordSetProvider()
    {
        return new ConnectorRecordSetProvider()
        {
            @Override
            public RecordSet getRecordSet(ConnectorTransactionHandle transaction, ConnectorSession session, ConnectorSplit split, ConnectorTableHandle table, List<? extends ColumnHandle> columns)
            {
                return new InMemoryRecordSet(ImmutableList.of(VARCHAR), ImmutableList.of());
            }
        };
    }

    @Override
    public ConnectorTransactionHandle beginTransaction(IsolationLevel isolationLevel, boolean readOnly, boolean autoCommit)
    {
        return TelemetryDataTransactionHandle.INSTANCE;
    }

    @Override
    public Optional<FunctionProvider> getFunctionProvider()
    {
        return Optional.of(new FunctionProvider()
        {
            @Override
            public TableFunctionProcessorProvider getTableFunctionProcessorProvider(ConnectorTableFunctionHandle functionHandle)
            {
                return new TableFunctionProcessorProvider()
                {
                    @Override
                    public TableFunctionSplitProcessor getSplitProcessor(ConnectorSession session, ConnectorTableFunctionHandle handle, ConnectorSplit split)
                    {
                        return new ReadSpansSplitProcessor(telemetryStore);
                    }
                };
            }
        });
    }
}
