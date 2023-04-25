package io.trino.plugin.telemetrycollector.connector;

import com.google.inject.Injector;
import io.airlift.bootstrap.Bootstrap;
import io.airlift.json.JsonModule;
import io.trino.plugin.base.TypeDeserializerModule;
import io.trino.plugin.telemetrycollector.TelemetryCollectorModule;
import io.trino.spi.connector.Connector;
import io.trino.spi.connector.ConnectorContext;
import io.trino.spi.connector.ConnectorFactory;

import java.util.Map;

import static io.trino.plugin.base.Versions.checkSpiVersion;
import static java.util.Objects.requireNonNull;

public class TelemetryDataConnectorFactory
        implements ConnectorFactory
{
    @Override
    public String getName()
    {
        return "telemetry_data";
    }

    @Override
    public Connector create(String catalogName, Map<String, String> config, ConnectorContext context)
    {
        requireNonNull(config, "config is null");
        checkSpiVersion(context, this);

        Bootstrap app = new Bootstrap(
                new JsonModule(),
                new TypeDeserializerModule(context.getTypeManager()),
                new TelemetryCollectorModule());

        Injector injector = app
                .doNotInitializeLogging()
                .setRequiredConfigurationProperties(config)
                .initialize();

        return injector.getInstance(TelemetryDataConnector.class);
    }
}
