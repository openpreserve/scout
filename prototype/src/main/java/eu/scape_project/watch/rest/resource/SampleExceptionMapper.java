package eu.scape_project.watch.rest.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import eu.scape_project.watch.utils.ApiResponse;
import eu.scape_project.watch.utils.exception.ApiException;
import eu.scape_project.watch.utils.exception.BadRequestException;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * Sample class to example an exception mapper.
 * 
 * @author Wordnik
 * 
 */
@Provider
public class SampleExceptionMapper implements ExceptionMapper<ApiException> {

  /**
   * @param exception
   *          The thrown exception
   * @return The response with error code.
   */
  public Response toResponse(final ApiException exception) {
    if (exception instanceof NotFoundException) {
      return Response.status(Status.NOT_FOUND).entity(new ApiResponse(ApiResponse.ERROR, exception.getMessage()))
        .build();
    } else if (exception instanceof BadRequestException) {
      return Response.status(Status.BAD_REQUEST).entity(new ApiResponse(ApiResponse.ERROR, exception.getMessage()))
        .build();
    } else {
      return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity(new ApiResponse(ApiResponse.ERROR, "a system error occured")).build();
    }
  }
}
