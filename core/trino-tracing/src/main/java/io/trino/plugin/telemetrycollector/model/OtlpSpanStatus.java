package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record OtlpSpanStatus(
        @JsonProperty("status_code") Optional<Long> statusCode)
{
}
