package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.listing.ApiListing;

@Path("/resources.json")
@Api(value = "/resources", description = "List of available operations")
@Produces({"application/json"})
public class ApiListingResourceJSON extends ApiListing {
}