package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record JaegerSpanLog(
        @JsonProperty("timestamp") long timestamp,
        @JsonProperty("fields") List<JaegerKeyValue> fields)
{
}
