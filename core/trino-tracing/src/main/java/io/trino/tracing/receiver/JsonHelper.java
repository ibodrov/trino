package io.trino.tracing.receiver;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.HexFormat;

import static com.google.protobuf.ByteString.copyFromUtf8;

public class JsonHelper
{
    // TODO is it common to use random ByteStrings for IDs?
    public static Span.Builder normalize(Span span)
    {
        return span.toBuilder()
                .setTraceId(toHex(span.getTraceId()))
                .setSpanId(toHex(span.getSpanId()))
                .setParentSpanId(toHex(span.getParentSpanId()));
    }

    public static String serializeSpan(Span span)
    {
        try {
            return JsonFormat.printer()
                    .omittingInsignificantWhitespace()
                    .print(span);
        }
        catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serializeSpanBuilder(Span.Builder builder)
    {
        try {
            return JsonFormat.printer()
                    .omittingInsignificantWhitespace()
                    .print(builder);
        }
        catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public static Span deserializeSpan(String json)
    {
        Span.Builder builder = Span.newBuilder();

        try {
            JsonFormat.parser()
                    .ignoringUnknownFields()
                    .merge(json, builder);

            return builder.build();
        }
        catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteString toHex(ByteString s)
    {
        return copyFromUtf8(HexFormat.of().formatHex(s.toByteArray()));
    }

    private JsonHelper()
    {
    }
}
