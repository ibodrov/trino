package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record JaegerTrace(
        @JsonProperty("traceID") String traceId,
        @JsonProperty("spans") List<JaegerSpan> spans,
        @JsonProperty("processes") Map<String, JaegerProcess> processes)
{
    // TODO processes
}
