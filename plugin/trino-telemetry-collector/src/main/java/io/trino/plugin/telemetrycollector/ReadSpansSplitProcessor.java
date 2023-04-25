package io.trino.plugin.telemetrycollector;

import com.google.common.collect.ImmutableList;
import io.trino.spi.Page;
import io.trino.spi.PageBuilder;
import io.trino.spi.block.BlockBuilder;
import io.trino.spi.ptf.TableFunctionProcessorState;
import io.trino.spi.ptf.TableFunctionSplitProcessor;

import static com.google.common.base.Preconditions.checkState;
import static io.trino.spi.ptf.TableFunctionProcessorState.Finished.FINISHED;
import static io.trino.spi.ptf.TableFunctionProcessorState.Processed.produced;
import static io.trino.spi.type.VarcharType.VARCHAR;

public class ReadSpansSplitProcessor
        implements TableFunctionSplitProcessor
{
    private final PageBuilder pageBuilder = new PageBuilder(ImmutableList.of(VARCHAR));

    private int filesLeft = 100;
    private boolean finished;

    @Override
    public TableFunctionProcessorState process()
    {
        checkState(pageBuilder.isEmpty(), "page builder not empty");

        if (finished) {
            return FINISHED;
        }

        BlockBuilder block = pageBuilder.getBlockBuilder(0);

        while (!pageBuilder.isFull() && filesLeft > 0) {
            pageBuilder.declarePosition();
            VARCHAR.writeString(block, "Hello!");
            filesLeft--;
        }
        if (!pageBuilder.isFull()) {
            finished = true;
        }

        Page page = pageBuilder.build();
        pageBuilder.reset();
        return produced(page);
    }
}
