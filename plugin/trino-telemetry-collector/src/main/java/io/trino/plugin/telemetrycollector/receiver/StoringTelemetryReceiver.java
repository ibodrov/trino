package io.trino.plugin.telemetrycollector.receiver;

import io.airlift.log.Logger;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.trace.v1.Span;

import javax.inject.Inject;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class StoringTelemetryReceiver
        implements TelemetryReceiver
{
    private final Logger log = Logger.get(StoringTelemetryReceiver.class);
    private final TelemetryStore store;

    @Inject
    public StoringTelemetryReceiver(TelemetryStore store)
    {
        this.store = requireNonNull(store, "store is null");
    }

    @Override
    public ExportTraceServiceResponse receiveTrace(ExportTraceServiceRequest request)
    {
        List<Span> spans = request.getResourceSpansList().stream()
                .flatMap(s -> s.getScopeSpansList().stream())
                .flatMap(s -> s.getSpansList().stream())
                .toList();

        store.saveSpans(spans);

        return ExportTraceServiceResponse.newBuilder()
                .build();
    }

    @Override
    public ExportMetricsServiceResponse receiveMetric(ExportMetricsServiceRequest request)
    {
        log.warn("Metrics are not supported yet");
        return ExportMetricsServiceResponse.newBuilder()
                .build();
    }

    @Override
    public ExportLogsServiceResponse receiveLog(ExportLogsServiceRequest request)
    {
        log.warn("Logs are not supported yet");
        return TelemetryReceiver.super.receiveLog(request);
    }
}
