package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JaegerKeyValue(
        @JsonProperty("key") String key,
        @JsonProperty("type") String type, // TODO enum
        @JsonProperty("value") String value)
{
}