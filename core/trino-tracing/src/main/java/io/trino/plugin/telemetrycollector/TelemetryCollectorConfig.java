package io.trino.plugin.telemetrycollector;

import io.airlift.configuration.Config;

public class TelemetryCollectorConfig
{
    private int grpcServerPort = 4317;

    public int getGrpcServerPort()
    {
        return grpcServerPort;
    }

    @Config("telemetry.collector.grpc-server-port")
    public void setGrpcServerPort(int grpcServerPort)
    {
        this.grpcServerPort = grpcServerPort;
    }
}
