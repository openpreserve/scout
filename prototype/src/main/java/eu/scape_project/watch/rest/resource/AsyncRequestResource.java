/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.dao.AsyncRequestDAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * REST API for {@link AsyncRequest} operations.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class AsyncRequestResource extends JavaHelp {

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

    final AsyncRequest request = AsyncRequestDAO.getInstance().findById(requestId);

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
  @Path("/")
  @ApiOperation(value = "Create Async Request", notes = "This can only be done by a logged user (TODO)")
  public Response createAsyncRequest(@ApiParam(value = "Async Request", required = true) final AsyncRequest request) {
    AsyncRequestDAO.getInstance().save(request);
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
    final Collection<AsyncRequest> list = AsyncRequestDAO.getInstance().list(start, max);
    return Response.ok().entity(new GenericEntity<Collection<AsyncRequest>>(list) {
    }).build();
  }

}
