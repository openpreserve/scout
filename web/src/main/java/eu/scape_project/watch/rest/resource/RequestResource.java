/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.query.QueryParseException;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.utils.exception.NotFoundException;

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
   * Get the result list of an asynchronous request.
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
  @Path("/list")
  @ApiOperation(value = "Make a request", notes = "")
  public Response getRequestList(
    @ApiParam(value = "Request target", required = true, allowableValues = "entity, entity_type, property, property_value") @QueryParam("target") final String target,
    @ApiParam(value = "Request query", required = true) @QueryParam("query") final String query,
    @ApiParam(value = "Start index", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Max number of items", required = true) @QueryParam("max") final int max) {

    LOG.debug("Making request '{}', target={}, start={}, max={}", new Object[] {query, target, start, max});

    final RequestTarget requestTarget = RequestTarget.valueOf(target.toUpperCase());

    final List<? extends RdfBean<?>> list = DAO.REQUEST.query(requestTarget, query, start, max);

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

  /**
   * Get the result count of an asynchronous request.
   * 
   * @param target
   *          The request target, as defined in {@link RequestTarget}
   * @param query
   *          The query bindings
   * @return The number of items of the list of the requested target object,
   *         filtered by the above constraints
   */
  @GET
  @Path("/count")
  @ApiOperation(value = "Make a request", notes = "")
  @ApiErrors(value = {@ApiError(code = 400, reason = "Query parse exception")})
  public Response getRequestCount(
    @ApiParam(value = "Request target", required = true, allowableValues = "entity, entity_type, property, property_value") @QueryParam("target") final String target,
    @ApiParam(value = "Request query", required = true) @QueryParam("query") final String query) {

    final RequestTarget requestTarget = RequestTarget.valueOf(target.toUpperCase());
    try {
      final int count = DAO.REQUEST.count(requestTarget, query);
      return Response.ok().entity(count).build();
    } catch (final QueryParseException e) {
      return Response.status(Status.BAD_REQUEST).build();
    }
  }
}
