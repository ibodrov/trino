package io.trino.tracing.receiver;

import io.opentelemetry.proto.trace.v1.Span;

import java.util.Collection;
import java.util.List;

public interface TelemetryStore
{
    void saveSpans(Collection<Span> span);

    List<Span> readSpans();
}
