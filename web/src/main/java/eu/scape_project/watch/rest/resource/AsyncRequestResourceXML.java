package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.annotations.Api;

/**
 * {@link AsyncRequestResource} with XML output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/asyncrequest.xml")
@Api(value = "/asyncrequest", description = "Operations about async requests")
@Singleton
@Produces({"application/xml"})
public class AsyncRequestResourceXML extends AsyncRequestResource {

}
