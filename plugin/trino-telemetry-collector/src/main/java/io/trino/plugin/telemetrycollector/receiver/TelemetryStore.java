package io.trino.plugin.telemetrycollector.receiver;

import io.opentelemetry.proto.trace.v1.Span;

import java.util.Collection;

public interface TelemetryStore
{
    void saveSpans(Collection<Span> span);

    Collection<Span> readSpans();
}
