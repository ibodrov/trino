package io.trino.plugin.telemetrycollector;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.trino.plugin.telemetrycollector.connector.TelemetryDataConnector;
import io.trino.plugin.telemetrycollector.connector.TelemetryDataMetadata;
import io.trino.plugin.telemetrycollector.receiver.OtlpGrpcServer;
import io.trino.plugin.telemetrycollector.receiver.StoringTelemetryReceiver;
import io.trino.plugin.telemetrycollector.receiver.TelemetryReceiver;
import io.trino.plugin.telemetrycollector.receiver.TelemetryStore;
import io.trino.plugin.telemetrycollector.receiver.TelemetryStoreProvider;
import io.trino.plugin.telemetrycollector.ui.JaegerApiResource;
import io.trino.plugin.telemetrycollector.ui.JaegerUIResource;

import static com.google.inject.Scopes.SINGLETON;
import static io.airlift.configuration.ConfigBinder.configBinder;
import static io.airlift.jaxrs.JaxrsBinder.jaxrsBinder;

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

        // OTLP receiver

        binder.bind(OtlpGrpcServer.class).in(SINGLETON);
        binder.bind(TelemetryReceiver.class).to(StoringTelemetryReceiver.class);

        // span store

        binder.bind(TelemetryStore.class).toProvider(TelemetryStoreProvider.class).in(SINGLETON);

        // UI

        jaxrsBinder(binder).bind(JaegerUIResource.class);
        jaxrsBinder(binder).bind(JaegerApiResource.class);
    }
}
