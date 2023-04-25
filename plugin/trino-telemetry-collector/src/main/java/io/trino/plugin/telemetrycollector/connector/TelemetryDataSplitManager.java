package io.trino.plugin.telemetrycollector.connector;

import com.google.common.collect.ImmutableList;
import io.trino.spi.HostAddress;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorSplit;
import io.trino.spi.connector.ConnectorSplitManager;
import io.trino.spi.connector.ConnectorSplitSource;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.connector.FixedSplitSource;
import io.trino.spi.ptf.ConnectorTableFunctionHandle;

import java.time.Instant;
import java.util.List;

public class TelemetryDataSplitManager
        implements ConnectorSplitManager
{
    @Override
    public ConnectorSplitSource getSplits(
            ConnectorTransactionHandle transaction,
            ConnectorSession session,
            ConnectorTableFunctionHandle function)
    {
        return new FixedSplitSource(new TelemetryDataConnectorSplit(Instant.now()));
    }

    public record TelemetryDataConnectorSplit(Instant createdAt)
            implements ConnectorSplit
    {
        @Override
        public long getRetainedSizeInBytes()
        {
            return 0;
        }

        @Override
        public boolean isRemotelyAccessible()
        {
            return true;
        }

        @Override
        public List<HostAddress> getAddresses()
        {
            return ImmutableList.of();
        }

        @Override
        public Object getInfo()
        {
            return null;
        }
    }
}
