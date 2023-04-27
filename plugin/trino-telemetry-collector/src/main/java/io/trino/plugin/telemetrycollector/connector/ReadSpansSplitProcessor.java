package io.trino.plugin.telemetrycollector.connector;

import com.google.common.collect.ImmutableList;
import io.airlift.log.Logger;
import io.opentelemetry.proto.trace.v1.Span;
import io.trino.plugin.telemetrycollector.receiver.JsonHelper;
import io.trino.plugin.telemetrycollector.receiver.TelemetryStore;
import io.trino.spi.Page;
import io.trino.spi.PageBuilder;
import io.trino.spi.ptf.TableFunctionProcessorState;
import io.trino.spi.ptf.TableFunctionSplitProcessor;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static io.trino.spi.ptf.TableFunctionProcessorState.Finished.FINISHED;
import static io.trino.spi.ptf.TableFunctionProcessorState.Processed.produced;
import static io.trino.spi.type.BigintType.BIGINT;
import static io.trino.spi.type.VarcharType.VARCHAR;

public class ReadSpansSplitProcessor
        implements TableFunctionSplitProcessor
{
    private final Logger log = Logger.get(ReadSpansSplitProcessor.class);
    private final PageBuilder pageBuilder = new PageBuilder(ImmutableList.of(VARCHAR, BIGINT, BIGINT, VARCHAR));

    private final TelemetryStore store;

    private int cursor;
    private boolean finished;
    private List<Span> data;

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
            data = store.readSpans();
            cursor = 0;
        }

        while (!pageBuilder.isFull() && cursor < data.size()) {
            Span span = data.get(cursor);

            pageBuilder.declarePosition();

            // trace_id
            VARCHAR.writeString(pageBuilder.getBlockBuilder(0), span.getTraceId().toStringUtf8());

            // start_ts
            BIGINT.writeLong(pageBuilder.getBlockBuilder(1), span.getStartTimeUnixNano());

            // end_ts
            BIGINT.writeLong(pageBuilder.getBlockBuilder(2), span.getEndTimeUnixNano());

            // span
            VARCHAR.writeString(pageBuilder.getBlockBuilder(3), JsonHelper.serializeSpan(span));

            cursor++;
        }

        if (!pageBuilder.isFull()) {
            finished = true;
        }

        Page page = pageBuilder.build();
        pageBuilder.reset();

        return produced(page);
    }
}
