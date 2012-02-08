package eu.scape_project.watch.core.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

@Path("/request.json")
@Api(value = "/request", description = "Operations about async requests")
@Singleton
@Produces({"application/json"})
public class RequestResourceJSON extends RequestResource {

}
