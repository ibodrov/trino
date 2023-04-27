package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record JaegerProcess(
        @JsonProperty("serviceName") String serviceName,
        @JsonProperty("tags") List<JaegerKeyValue> tags)
{
}
