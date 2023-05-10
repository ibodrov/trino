package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record OtlpValue(
        @JsonProperty("int_value") Optional<Long> intValue)
{
}
