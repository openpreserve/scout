package eu.scape_project.watch.core.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

@Path("/entity.xml")
@Api(value = "/entity", description = "Operations about Entities")
@Singleton
@Produces({"application/xml"})
public class EntityResourceXML extends EntityResource {

}
