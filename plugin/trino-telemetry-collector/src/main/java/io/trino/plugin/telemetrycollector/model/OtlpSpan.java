package io.trino.plugin.telemetrycollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OtlpSpan(
        @JsonProperty("trace_id") String traceId,
        @JsonProperty("span_id") String spanId,
        @JsonProperty("parent_span_id") String parentSpanId,
        @JsonProperty("name") String name,
        @JsonProperty("kind") String kind,
        @JsonProperty("start_time_unix_nano") long startTimeUnixNano,
        @JsonProperty("end_time_unix_nano") long endTimeUnixNano,
        @JsonProperty("events") List<OtlpSpanEvent> events,
        @JsonProperty("status") OtlpSpanStatus status)
{
}
