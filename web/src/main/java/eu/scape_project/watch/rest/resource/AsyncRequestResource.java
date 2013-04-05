/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * REST API for {@link AsyncRequest} operations.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class AsyncRequestResource {

  /**
   * Get an existing {@link AsyncRequest}.
   * 
   * @param requestId
   *          The {@link AsyncRequest} id
   * @return The {@link AsyncRequest} or throws {@link NotFoundException} if not
   *         found.
   */
  @GET
  @Path("/{id}")
  @ApiOperation(value = "Find Request by Id", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Request not found")})
  public Response getAsyncRequestById(
    @ApiParam(value = "Request Id", required = true) @PathParam("id") final String requestId) {

    final AsyncRequest request = DAO.ASYNC_REQUEST.findById(requestId);

    if (request != null) {
      return Response.ok().entity(request).build();
    } else {
      throw new NotFoundException("Request not found: " + requestId);
    }
  }

  /**
   * Create a new {@link AsyncRequest}.
   * 
   * @param request
   *          The async request to save
   * @return The created async request
   */
  @POST
  @Path("/new")
  @ApiOperation(value = "Create Async Request", notes = "This can only be done by a logged user (TODO)")
  public Response createAsyncRequest(@ApiParam(value = "Async Request", required = true) final AsyncRequest request) {
    DAO.save(request);
    return Response.ok().entity(request).build();
  }

  /**
   * List all {@link AsyncRequest} in KB.
   * 
   * @param start
   *          The index of the first item to retrieve
   * 
   * @param max
   *          The maximum number of items to retrieve
   * 
   * @return A list will all {@link AsyncRequest}.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all async requests", notes = "")
  public Response listAsyncRequest(
    @ApiParam(value = "Index of first item to retrieve", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Maximum number of items to retrieve", required = true) @QueryParam("max") final int max) {
    final Collection<AsyncRequest> list = DAO.ASYNC_REQUEST.list(start, max);
    return Response.ok().entity(new GenericEntity<Collection<AsyncRequest>>(list) {
    }).build();
  }

  @DELETE
  @Path("/{id}")
  @ApiOperation(value = "Delete request by id", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Request not found")})
  public Response deleteAsyncRequestById(
    @ApiParam(value = "Request Id", required = true) @PathParam("id") final String requestId) {

    final AsyncRequest request = DAO.ASYNC_REQUEST.findById(requestId);

    if (request != null) {
      DAO.delete(request);
      return Response.ok().entity(request).build();
    } else {
      throw new NotFoundException("Request not found: " + requestId);
    }
  }

}
