package io.trino.plugin.telemetrycollector;

import com.google.common.collect.ImmutableList;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.ptf.AbstractConnectorTableFunction;
import io.trino.spi.ptf.Argument;
import io.trino.spi.ptf.ArgumentSpecification;
import io.trino.spi.ptf.ConnectorTableFunction;
import io.trino.spi.ptf.Descriptor;
import io.trino.spi.ptf.Descriptor.Field;
import io.trino.spi.ptf.ReturnTypeSpecification;
import io.trino.spi.ptf.ReturnTypeSpecification.DescribedTable;
import io.trino.spi.ptf.TableFunctionAnalysis;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.trino.spi.type.VarcharType.createUnboundedVarcharType;

public class ReadSpansTableFunction
        extends AbstractConnectorTableFunction
{
    private static final Descriptor DESCRIPTOR = new Descriptor(ImmutableList.of(
            new Field("span", Optional.of(createUnboundedVarcharType())))
    );

}
