package io.trino.plugin.telemetrycollector.connector;

import com.google.common.collect.ImmutableList;
import io.airlift.log.Logger;
import io.trino.plugin.telemetrycollector.receiver.JsonHelper;
import io.trino.plugin.telemetrycollector.receiver.TelemetryStore;
import io.trino.spi.Page;
import io.trino.spi.PageBuilder;
import io.trino.spi.block.BlockBuilder;
import io.trino.spi.ptf.TableFunctionProcessorState;
import io.trino.spi.ptf.TableFunctionSplitProcessor;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static io.trino.spi.ptf.TableFunctionProcessorState.Finished.FINISHED;
import static io.trino.spi.ptf.TableFunctionProcessorState.Processed.produced;
import static io.trino.spi.type.VarcharType.VARCHAR;

public class ReadSpansSplitProcessor
        implements TableFunctionSplitProcessor
{
    private final Logger log = Logger.get(ReadSpansSplitProcessor.class);
    private final PageBuilder pageBuilder = new PageBuilder(ImmutableList.of(VARCHAR));

    private final TelemetryStore store;

    private int index;
    private boolean finished;
    private List<String> data;

    public ReadSpansSplitProcessor(TelemetryStore store)
    {
        this.store = store;
    }

    @Override
    public TableFunctionProcessorState process()
    {
        checkState(pageBuilder.isEmpty(), "page builder not empty");

        if (finished) {
            data = null;
            return FINISHED;
        }

        if (data == null) {
            log.info("Loading data...");
            // TODO use strings, avoid extra serialization
            data = store.readSpans().stream().map(JsonHelper::serializeSpan).toList();
            index = 0;
        }

        BlockBuilder block = pageBuilder.getBlockBuilder(0);

        while (!pageBuilder.isFull() && index < data.size()) {
            pageBuilder.declarePosition();
            VARCHAR.writeString(block, data.get(index));
            index++;
        }
        if (!pageBuilder.isFull()) {
            finished = true;
        }

        Page page = pageBuilder.build();
        pageBuilder.reset();
        return produced(page);
    }
}
