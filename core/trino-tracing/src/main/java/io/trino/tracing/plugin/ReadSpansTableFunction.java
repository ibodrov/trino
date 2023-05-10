package io.trino.tracing.plugin;

import com.google.common.collect.ImmutableList;
import io.trino.spi.connector.ConnectorAccessControl;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.ptf.AbstractConnectorTableFunction;
import io.trino.spi.ptf.Argument;
import io.trino.spi.ptf.ConnectorTableFunctionHandle;
import io.trino.spi.ptf.Descriptor;
import io.trino.spi.ptf.ReturnTypeSpecification.DescribedTable;
import io.trino.spi.ptf.TableFunctionAnalysis;

import java.util.Map;

import static io.trino.spi.type.BigintType.BIGINT;
import static io.trino.spi.type.VarcharType.VARCHAR;

public class ReadSpansTableFunction
        extends AbstractConnectorTableFunction
{
    private static final Descriptor RETURNED_TYPE = Descriptor.descriptor(
            ImmutableList.of("trace_id", "start_ts", "end_ts", "span"),
            ImmutableList.of(VARCHAR, BIGINT, BIGINT, VARCHAR));

    public ReadSpansTableFunction()
    {
        super("telemetry", "read_spans", ImmutableList.of(), new DescribedTable(RETURNED_TYPE));
    }

    @Override
    public TableFunctionAnalysis analyze(ConnectorSession session, ConnectorTransactionHandle transaction, Map<String, Argument> arguments, ConnectorAccessControl accessControl)
    {
        return TableFunctionAnalysis.builder()
                .handle(new ReadSpansFunctionHandle())
                .build();
    }

    public static class ReadSpansFunctionHandle
            implements ConnectorTableFunctionHandle
    {
    }
}
