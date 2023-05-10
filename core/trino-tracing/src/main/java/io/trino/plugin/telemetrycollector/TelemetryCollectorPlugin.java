package io.trino.plugin.telemetrycollector;

import com.google.common.collect.ImmutableList;
import io.trino.plugin.telemetrycollector.connector.TelemetryDataConnectorFactory;
import io.trino.spi.Plugin;
import io.trino.spi.connector.ConnectorFactory;

public class TelemetryCollectorPlugin
        implements Plugin
{
    @Override
    public Iterable<ConnectorFactory> getConnectorFactories()
    {
        return ImmutableList.of(new TelemetryDataConnectorFactory());
    }
}
