package eu.scape_project.watch.core.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

@Path("/property.xml")
@Api(value = "/property", description = "Operations about Properties")
@Singleton
@Produces({ "application/xml" })
public class PropertyResourceXML extends PropertyResource {

}
