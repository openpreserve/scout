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

import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.QueryBinding;
import eu.scape_project.watch.domain.RequestTarget;

/**
 * REST API for Synchronous Requests.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class RequestResource  {

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
    @ApiParam(value = "Initial bindings") @QueryParam("binding") final List<QueryBinding> queryBindings,
    @ApiParam(value = "Start index", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Max number of items", required = true) @QueryParam("max") final int max) {

    LOG.info("Making request '{}', bindings={}, target={}, start={}, max={}", new Object[] {query, queryBindings,
      target, start, max});

    final RequestTarget requestTarget = RequestTarget.valueOf(target.toUpperCase());
    final QuerySolutionMap bindings = DAO.QUESTION_TEMPLATE_PARAMETER.parseBindings(queryBindings);
    final List<? extends RdfBean<?>> list = DAO.REQUEST.query(requestTarget, query, bindings, start, max);

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
      case MEASUREMENT:
        ret = Response.ok().entity(new GenericEntity<List<Measurement>>((List<Measurement>) list) {
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
    @ApiParam(value = "Request query", required = true) @QueryParam("query") final String query,
    @ApiParam(value = "Initial bindings") @QueryParam("binding") final List<QueryBinding> queryBindings) {

    final RequestTarget requestTarget = RequestTarget.valueOf(target.toUpperCase());
    final QuerySolutionMap bindings = DAO.QUESTION_TEMPLATE_PARAMETER.parseBindings(queryBindings);
    try {
      final int count = DAO.REQUEST.count(requestTarget, query, bindings);
      return Response.ok().entity(count).build();
    } catch (final QueryParseException e) {
      return Response.status(Status.BAD_REQUEST).build();
    }
  }

  @GET
  @Path("/sparql")
  @ApiOperation(value = "Direct sparql interface", notes = "")
  public Response sparql(
    @ApiParam(value = "Request query", required = true) @QueryParam("query") final String queryString) {

    LOG.info("SPARQL: {}", queryString);
    
    Model model = Jenabean.instance().model();
    Query query = QueryFactory.create(queryString);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    String result = "";
    try {
      ResultSet results = qexec.execSelect();
      result = ResultSetFormatter.asXMLString(results);
    } finally {
      qexec.close();
    }
    return Response.ok().entity(result).build();
  }
}
