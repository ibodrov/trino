package io.trino.tracing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record JaegerSpan(
        @JsonProperty("traceID") String traceId,
        @JsonProperty("spanID") String spanId,
        @JsonProperty("operationName") String operationName,
        @JsonProperty("references") List<JaegerSpanReference> jaegerSpanReference,
        @JsonProperty("startTime") long startTime,
        @JsonProperty("duration") long duration,
        @JsonProperty("tags") List<JaegerKeyValue> tags,
        @JsonProperty("logs") List<JaegerSpanLog> logs,
        @JsonProperty("processID") String processId,
        @JsonProperty("warnings") List<String> warnings)

{
}
