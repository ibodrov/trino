package io.trino.plugin.telemetrycollector.ui;

import com.google.common.collect.ImmutableList;
import io.trino.plugin.telemetrycollector.model.JaegerTrace;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api")
public class JaegerApiResource
{

    @GET
    @Path("services")
    @Produces(APPLICATION_JSON)

    public ListResponse<String> services()
    {
        return ListResponse.of("demo-service");
    }

    @GET
    @Path("services/{service}/operations")
    @Produces(APPLICATION_JSON)
    public ListResponse<String> operations(@PathParam("service") String service)
    {
        return ListResponse.of("demo-operation");
    }

    @GET
    @Path("traces")
    @Produces(APPLICATION_JSON)
    public ListResponse<JaegerTrace> traces(
            @QueryParam("traceID") String traceId,
            @QueryParam("service") String service,
            @QueryParam("operation") String operation,
            @QueryParam("start") long start,
            @QueryParam("end") long end,
            @QueryParam("maxDuration") String maxDuration,
            @QueryParam("minDuration") String minDuration,
            @QueryParam("lookback") String lookBack,
            @QueryParam("limit") long limit)
    {
        // TODO
        return ListResponse.of(ImmutableList.of());
    }
}
