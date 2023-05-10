package io.trino.tracing.ui;

import java.util.List;
import java.util.Optional;

public record ListResponse<T>(List<T> data,
                              long total,
                              long limit,
                              long offset,
                              Optional<List<String>> errors)
{
    @SafeVarargs
    public static <T> ListResponse<T> of(T... elements)
    {
        return new ListResponse<>(List.of(elements), elements.length, 0, 0, Optional.empty());
    }

    public static <T> ListResponse<T> of(List<T> data)
    {
        return new ListResponse<>(data, data.size(), 0, 0, Optional.empty());
    }
}
