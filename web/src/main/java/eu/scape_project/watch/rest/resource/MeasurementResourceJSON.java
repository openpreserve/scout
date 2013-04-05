package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;
import com.wordnik.swagger.annotations.Api;

/**
 * {@link MeasurementResource} with JSON output.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Path("/measurement.json")
@Api(value = "/measurement", description = "Operations about Measurements")
@Singleton
@Produces({"application/json"})
public class MeasurementResourceJSON extends MeasurementResource {

}
