package io.trino.plugin.telemetrycollector;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.trino.plugin.telemetrycollector.connector.TelemetryDataConnector;
import io.trino.plugin.telemetrycollector.connector.TelemetryDataMetadata;
import io.trino.plugin.telemetrycollector.receiver.TelemetryStore;
import io.trino.plugin.telemetrycollector.receiver.TelemetryStoreProvider;
import io.trino.plugin.telemetrycollector.ui.JaegerApiResource;
import io.trino.plugin.telemetrycollector.ui.JaegerUIResource;

import static com.google.inject.Scopes.SINGLETON;
import static io.airlift.configuration.ConfigBinder.configBinder;
import static io.airlift.jaxrs.JaxrsBinder.jaxrsBinder;

// TODO consider moving stuff into trino-main
public class TelemetryCollectorModule
        implements Module
{
    @Override
    public void configure(Binder binder)
    {
        configBinder(binder).bindConfig(TelemetryCollectorConfig.class);

        // connector
        binder.bind(TelemetryDataConnector.class).in(SINGLETON);
        binder.bind(TelemetryDataMetadata.class).in(SINGLETON);

        // span store

        binder.bind(TelemetryStore.class).toProvider(TelemetryStoreProvider.class).in(SINGLETON);

        // UI

        jaxrsBinder(binder).bind(JaegerUIResource.class);
        jaxrsBinder(binder).bind(JaegerApiResource.class);
    }
}
