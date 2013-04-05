package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.annotations.Api;

/**
 * {@link EntityTypeResource} with XML output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/entitytype.xml")
@Api(value = "/entitytype", description = "Operations about Entity Types")
@Singleton
@Produces({"application/xml"})
public class EntityTypeResourceXML extends EntityTypeResource {

}
