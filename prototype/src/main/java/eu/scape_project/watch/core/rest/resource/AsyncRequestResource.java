/**
 * 
 */
package eu.scape_project.watch.core.rest.resource;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;
import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.dao.AsyncRequestDAO;
import eu.scape_project.watch.core.model.AsyncRequest;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

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

    final AsyncRequest request = AsyncRequestDAO.findById(requestId);

    if (request != null) {
      return Response.ok().entity(request).build();
    } else {
      throw new NotFoundException("Request not found: " + requestId);
    }
  }

  /**
   * List all {@link AsyncRequest} in KB.
   * 
   * @return A list will all {@link AsyncRequest}.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all async requests", notes = "")
  public Response listAsyncRequest() {
    final Collection<AsyncRequest> list = KB.getInstance().getReader().load(AsyncRequest.class);
    return Response.ok().entity(new GenericEntity<Collection<AsyncRequest>>(list) {
    }).build();
  }

}
