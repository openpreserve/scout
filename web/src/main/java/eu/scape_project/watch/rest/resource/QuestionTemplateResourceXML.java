package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.annotations.Api;

/**
 * {@link QuestionTemplateResource} with XML output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/questiontemplate.xml")
@Api(value = "/questiontemplate", description = "Operations about question templates")
@Singleton
@Produces({"application/xml"})
public class QuestionTemplateResourceXML extends QuestionTemplateResource {

}
