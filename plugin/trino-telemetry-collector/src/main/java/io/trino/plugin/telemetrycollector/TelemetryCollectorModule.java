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
        binder.bind(DataAccessConnector.class).in(Scopes.SINGLETON);
        binder.bind(DataAccessMetadata.class).in(Scopes.SINGLETON);
    }
}
