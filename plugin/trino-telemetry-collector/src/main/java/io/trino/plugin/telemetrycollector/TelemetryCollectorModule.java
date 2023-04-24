package io.trino.plugin.telemetrycollector;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class TelemetryCollectorModule
        implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(TelemetryDataConnector.class).in(Scopes.SINGLETON);
        binder.bind(TelemetryDataMetadata.class).in(Scopes.SINGLETON);
    }
}
