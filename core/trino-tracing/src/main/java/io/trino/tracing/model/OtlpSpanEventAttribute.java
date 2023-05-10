package io.trino.tracing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OtlpSpanEventAttribute(
        @JsonProperty("key") String key,
        @JsonProperty("value") OtlpValue value
)
{
}
