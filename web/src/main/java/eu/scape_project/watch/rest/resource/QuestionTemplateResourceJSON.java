package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.core.Api;

/**
 * {@link QuestionTemplateResource} with JSON output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/questiontemplate.json")
@Api(value = "/questiontemplate", description = "Operations about question templates")
@Singleton
@Produces({"application/json"})
public class QuestionTemplateResourceJSON extends QuestionTemplateResource {

}
