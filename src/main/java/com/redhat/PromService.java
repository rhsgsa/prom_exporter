package com.redhat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v1")
@RegisterRestClient
@RegisterClientHeaders(AuthHeaderFactory.class)
public interface PromService {
    
    @GET
    @Path("/query")
    public PromResponse query(@QueryParam("query") String query); 
    @GET
    @Path("/query_range")
    public PromResponse queryRange(@QueryParam("query") String query,@QueryParam("start") String start,@QueryParam("end") String end,@QueryParam("step") String step); 
}
