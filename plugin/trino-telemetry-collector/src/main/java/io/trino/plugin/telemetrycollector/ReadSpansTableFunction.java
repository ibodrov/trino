package io.trino.plugin.telemetrycollector;

import com.google.common.collect.ImmutableList;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.ptf.AbstractConnectorTableFunction;
import io.trino.spi.ptf.Argument;
import io.trino.spi.ptf.ConnectorTableFunctionHandle;
import io.trino.spi.ptf.Descriptor;
import io.trino.spi.ptf.ReturnTypeSpecification.DescribedTable;
import io.trino.spi.ptf.TableFunctionAnalysis;

import java.util.Map;

import static io.trino.spi.type.VarcharType.VARCHAR;

public class ReadSpansTableFunction
        extends AbstractConnectorTableFunction
{
    private static final Descriptor RETURNED_TYPE = Descriptor.descriptor(
            ImmutableList.of("span"),
            ImmutableList.of(VARCHAR));

    public ReadSpansTableFunction()
    {
        super("hello", "read_spans", ImmutableList.of(), new DescribedTable(RETURNED_TYPE));
    }

    @Override
    public TableFunctionAnalysis analyze(
            ConnectorSession session,
            ConnectorTransactionHandle transaction,
            Map<String, Argument> arguments)
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
