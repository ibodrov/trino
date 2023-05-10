package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JaegerSpanReference(
        @JsonProperty("refType") JaegerSpanRefType refType,
        @JsonProperty("traceID") String traceId,
        @JsonProperty("spanID") String spanId)
{
}
