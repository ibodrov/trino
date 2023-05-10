package io.trino.plugin.telemetrycollector.receiver;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;

// TODO pass StreamObserver<Response> instead?
public interface TelemetryReceiver
{
    default ExportTraceServiceResponse receiveTrace(ExportTraceServiceRequest request)
    {
        return ExportTraceServiceResponse.newBuilder()
                .build();
    }

    default ExportMetricsServiceResponse receiveMetric(ExportMetricsServiceRequest request)
    {
        return ExportMetricsServiceResponse.newBuilder()
                .build();
    }

    default ExportLogsServiceResponse receiveLog(ExportLogsServiceRequest request)
    {
        return ExportLogsServiceResponse.newBuilder()
                .build();
    }
}
