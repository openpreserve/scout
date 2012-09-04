package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

/**
 * {@link PluginResource} with JSON output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/plugin.json")
@Api(value = "/plugin", description = "Operations about Plug-ins")
@Singleton
@Produces({"application/json"})
public class PluginResourceJSON extends PluginResource {

}
