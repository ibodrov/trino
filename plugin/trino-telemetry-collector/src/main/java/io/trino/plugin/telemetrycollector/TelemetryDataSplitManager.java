package io.trino.plugin.telemetrycollector;

import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorSplitManager;
import io.trino.spi.connector.ConnectorSplitSource;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.ptf.ConnectorTableFunctionHandle;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TelemetryDataSplitManager
        implements ConnectorSplitManager
{
    @Override
    public ConnectorSplitSource getSplits(
            ConnectorTransactionHandle transaction,
            ConnectorSession session,
            ConnectorTableFunctionHandle function)
    {
        return new TelemetryDataSplitSource();
    }

    public static class TelemetryDataSplitSource
            implements ConnectorSplitSource
    {
        @Override
        public CompletableFuture<ConnectorSplitBatch> getNextBatch(int maxSize)
        {
            return CompletableFuture.completedFuture(new ConnectorSplitBatch(List.of(), true));
        }

        @Override
        public void close()
        {

        }

        @Override
        public boolean isFinished()
        {
            return true;
        }
    }
}
