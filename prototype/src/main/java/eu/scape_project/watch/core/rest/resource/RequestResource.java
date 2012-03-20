/**
 * 
 */
package eu.scape_project.watch.core.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.dao.RequestDAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.RequestTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.RdfBean;

/**
 * REST API for Synchronous Requests.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class RequestResource extends JavaHelp {

  /**
   * The Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RequestResource.class);

  /**
   * Get the result of an asynchronous request.
   * 
   * @param target
   *          The request target, as defined in {@link RequestTarget}
   * @param query
   *          The query bindings
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of the requested target object, filtered by the above
   *         constraints
   */
  @SuppressWarnings("unchecked")
  @GET
  @Path("/{target}")
  @ApiOperation(value = "Make a request", notes = "")
  public Response getRequest(
    @ApiParam(value = "Request target", required = true, allowableValues = "entity, entity_type, property, property_value") @PathParam("target") final String target,
    @ApiParam(value = "Request query", required = true) @QueryParam("query") final String query,
    @ApiParam(value = "Start index", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Max number of items", required = true) @QueryParam("max") final int max) {

    LOG.debug("Making request '{}', target={}, start={}, max={}", new Object[] {query, target, start, max});

    final RequestTarget requestTarget = RequestTarget.valueOf(target.toUpperCase());

    final List<? extends RdfBean<?>> list = RequestDAO.getInstance().query(requestTarget, query, start, max);

    Response ret;
    switch (requestTarget) {
      case ENTITY_TYPE:
        ret = Response.ok().entity(new GenericEntity<List<EntityType>>((List<EntityType>) list) {
        }).build();
        break;
      case PROPERTY:
        ret = Response.ok().entity(new GenericEntity<List<Property>>((List<Property>) list) {
        }).build();
        break;
      case ENTITY:
        ret = Response.ok().entity(new GenericEntity<List<Entity>>((List<Entity>) list) {
        }).build();
        break;
      case PROPERTY_VALUE:
        ret = Response.ok().entity(new GenericEntity<List<PropertyValue>>((List<PropertyValue>) list) {
        }).build();
        break;
      default:
        LOG.error("Request target not supported {}", requestTarget);
        ret = null;
        break;
    }

    return ret;
  }
}
