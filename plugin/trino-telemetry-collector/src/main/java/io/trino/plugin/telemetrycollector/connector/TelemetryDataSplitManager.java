package io.trino.plugin.telemetrycollector.connector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.trino.plugin.telemetrycollector.connector.ReadSpansTableFunction.ReadSpansFunctionHandle;
import io.trino.spi.HostAddress;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorSplit;
import io.trino.spi.connector.ConnectorSplitManager;
import io.trino.spi.connector.ConnectorSplitSource;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.connector.FixedSplitSource;
import io.trino.spi.predicate.NullableValue;
import io.trino.spi.ptf.ConnectorTableFunctionHandle;

import java.time.Instant;
import java.util.List;

import static io.airlift.slice.Slices.utf8Slice;
import static io.trino.spi.predicate.TupleDomain.fromFixedValues;
import static io.trino.spi.type.VarcharType.createUnboundedVarcharType;
import static java.util.stream.Collectors.toList;

public class TelemetryDataSplitManager
        implements ConnectorSplitManager
{


    @Override
    public ConnectorSplitSource getSplits(
            ConnectorTransactionHandle transaction,
            ConnectorSession session,
            ConnectorTableFunctionHandle function)
    {
        if (!(function instanceof ReadSpansFunctionHandle)) {
            throw new IllegalStateException("Unexpected handle type");
        }


        /*
        nodeManager.getAllNodes().stream()
                .filter(node -> {
                    NullableValue value = NullableValue.of(createUnboundedVarcharType(), utf8Slice(node.getNodeIdentifier()));
                    return nodeFilter.overlaps(fromFixedValues(ImmutableMap.of(nodeColumnHandle.get(), value)));
                })
                .map(node -> new JmxSplit(ImmutableList.of(node.getHostAndPort())))
                .collect(toList());
         */

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
            return true; // TODO false
        }

        @Override
        public List<HostAddress> getAddresses()
        {
            return ImmutableList.of(); // TODO node addresses
        }

        @Override
        public Object getInfo()
        {
            return null;
        }
    }
}
