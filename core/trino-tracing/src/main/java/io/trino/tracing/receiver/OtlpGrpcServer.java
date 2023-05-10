package io.trino.tracing.receiver;

import io.airlift.log.Logger;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;
import io.opentelemetry.proto.collector.logs.v1.LogsServiceGrpc;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.trino.tracing.TracingConfig;
import io.trino.spi.NodeManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

public class OtlpGrpcServer
{
    private final Logger log = Logger.get(OtlpGrpcServer.class);

    private final Server grpcServer;
    private final TelemetryReceiver receiver;
    private final NodeManager nodeManager;

    @Inject
    public OtlpGrpcServer(
            TelemetryReceiver receiver,
            TracingConfig config,
            NodeManager nodeManager)
    {
        this.receiver = requireNonNull(receiver, "receiver is null");
        this.nodeManager = requireNonNull(nodeManager, "nodeManager is null");
        this.grpcServer = Grpc.newServerBuilderForPort(config.getGrpcServerPort(),
                        /* TODO */ InsecureServerCredentials.create())
                .addService(new TraceService())
                .addService(new MetricService())
                .addService(new LogService())
                .build();
    }

    @PostConstruct
    public synchronized void start()
            throws Exception
    {
        if (!nodeManager.getCurrentNode().isCoordinator()) {
            log.info("OLTP receiver disabled (the current node is not the coordinator node)");
            return;
        }

        grpcServer.start();
        log.info("OTLP receiver started");
    }

    @PreDestroy
    public synchronized void stop()
            throws InterruptedException
    {
        grpcServer.shutdown();
        grpcServer.awaitTermination(5, SECONDS);
        log.info("OTLP receiver stopped");
    }

    private class TraceService
            extends TraceServiceGrpc.TraceServiceImplBase
    {

        @Override
        public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver)
        {
            responseObserver.onNext(receiver.receiveTrace(request));
            responseObserver.onCompleted();
        }
    }

    private class MetricService
            extends MetricsServiceGrpc.MetricsServiceImplBase
    {

        @Override
        public void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> responseObserver)
        {
            responseObserver.onNext(receiver.receiveMetric(request));
            responseObserver.onCompleted();
        }
    }

    private class LogService
            extends LogsServiceGrpc.LogsServiceImplBase
    {
        @Override
        public void export(ExportLogsServiceRequest request, StreamObserver<ExportLogsServiceResponse> responseObserver)
        {
            responseObserver.onNext(receiver.receiveLog(request));
            responseObserver.onCompleted();
        }
    }
}
