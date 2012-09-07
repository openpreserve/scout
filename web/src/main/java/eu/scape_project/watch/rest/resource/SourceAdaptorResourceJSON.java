package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

/**
 * {@link SourceAdaptorResource} with JSON output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/sourceadaptor.json")
@Api(value = "/sourceadaptor", description = "Operations about Source Adaptors")
@Singleton
@Produces({"application/json"})
public class SourceAdaptorResourceJSON extends SourceAdaptorResource {

}
