package eu.scape_project.watch.rest.resource;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * {@link RequestResource} with XML output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/request.xml")
@Api(value = "/request", description = "Operations about requests")
@Singleton
@Produces({"application/xml"})
public class RequestResourceXML extends RequestResource {

}
