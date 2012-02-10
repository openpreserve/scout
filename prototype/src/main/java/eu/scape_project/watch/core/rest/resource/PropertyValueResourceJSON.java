package eu.scape_project.watch.core.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

@Path("/propertyvalue.json")
@Api(value = "/propertyvalue", description = "Operations about property values")
@Singleton
@Produces({"application/json"})
public class PropertyValueResourceJSON extends PropertyValueResource {

}
