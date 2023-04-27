package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OtlpSpanEvent(
        @JsonProperty("name") String name,
        @JsonProperty("time_unix_nano") long timeUnixNano,
        @JsonProperty("attributes") List<OtlpSpanEventAttribute> attributes)
{
}
