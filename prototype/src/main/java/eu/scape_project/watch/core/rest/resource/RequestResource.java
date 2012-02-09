/**
 * 
 */
package eu.scape_project.watch.core.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.core.rest.data.AsyncRequestData;
import eu.scape_project.watch.core.rest.exception.NotFoundException;
import eu.scape_project.watch.core.rest.model.AsyncRequest;

/**
 * @author lfaria
 * 
 */
public class RequestResource extends JavaHelp {

	private static AsyncRequestData asyncRequestData = new AsyncRequestData();

	@GET
	@Path("/{requestId}")
	@ApiOperation(value = "Find AsyncRequest by ID", notes = "ID is a sequential number starting at 0")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Pet not found") })
	public Response getRequestById(
			@ApiParam(value = "ID of the Request", required = true) @PathParam("requestId") String requestId)
			throws NotFoundException {
		AsyncRequest asyncRequest = asyncRequestData.getAsyncRequestById(Long
				.parseLong(requestId));
		if (asyncRequest != null) {
			return Response.ok().entity(asyncRequest).build();
		} else {
			throw new NotFoundException("AsyncRequest id not found: "
					+ requestId);
		}
	}
}
