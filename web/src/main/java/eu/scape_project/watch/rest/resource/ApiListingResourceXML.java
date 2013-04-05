package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.listing.ApiListing;

@Path("/resources.xml")
@Api(value = "/resources", description = "List of available operations")
@Produces({"application/xml"})
public class ApiListingResourceXML extends ApiListing {
}