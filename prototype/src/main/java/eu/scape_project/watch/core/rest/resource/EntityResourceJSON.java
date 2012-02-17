package eu.scape_project.watch.core.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

/**
 * {@link EntityResource} with JSON output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/entity.json")
@Api(value = "/entity", description = "Operations about Entities")
@Singleton
@Produces({"application/json"})
public class EntityResourceJSON extends EntityResource {

}
