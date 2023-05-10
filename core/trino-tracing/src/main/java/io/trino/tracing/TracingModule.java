package io.trino.tracing;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.trino.tracing.plugin.TracingDataConnector;
import io.trino.tracing.plugin.TracingDataMetadata;
import io.trino.tracing.receiver.OtlpGrpcServer;
import io.trino.tracing.receiver.StoringTelemetryReceiver;
import io.trino.tracing.receiver.TelemetryReceiver;
import io.trino.tracing.receiver.TelemetryStore;
import io.trino.tracing.receiver.TelemetryStoreProvider;
import io.trino.tracing.ui.JaegerApiResource;
import io.trino.tracing.ui.JaegerUIResource;

import static com.google.inject.Scopes.SINGLETON;
import static io.airlift.configuration.ConfigBinder.configBinder;
import static io.airlift.jaxrs.JaxrsBinder.jaxrsBinder;

public class TracingModule
        implements Module
{
    @Override
    public void configure(Binder binder)
    {
        configBinder(binder).bindConfig(TracingConfig.class);

        // connector

        binder.bind(TracingDataConnector.class).in(SINGLETON);
        binder.bind(TracingDataMetadata.class).in(SINGLETON);

        // span store

        binder.bind(TelemetryStore.class).toProvider(TelemetryStoreProvider.class).in(SINGLETON);

        // receiver

        binder.bind(OtlpGrpcServer.class).in(SINGLETON);
        binder.bind(TelemetryReceiver.class).to(StoringTelemetryReceiver.class);

        // UI

        jaxrsBinder(binder).bind(JaegerUIResource.class);
        jaxrsBinder(binder).bind(JaegerApiResource.class);
    }
}
