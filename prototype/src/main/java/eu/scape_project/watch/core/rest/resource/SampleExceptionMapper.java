package eu.scape_project.watch.core.rest.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import eu.scape_project.watch.core.rest.exception.ApiException;
import eu.scape_project.watch.core.rest.exception.BadRequestException;
import eu.scape_project.watch.core.rest.exception.NotFoundException;
import eu.scape_project.watch.core.rest.model.ApiResponse;

@Provider
public class SampleExceptionMapper implements ExceptionMapper<ApiException> {
  public Response toResponse(ApiException exception) {
    if (exception instanceof NotFoundException) {
      return Response.status(Status.NOT_FOUND).entity(new ApiResponse(ApiResponse.ERROR, exception.getMessage()))
        .build();
    } else if (exception instanceof BadRequestException) {
      return Response.status(Status.BAD_REQUEST).entity(new ApiResponse(ApiResponse.ERROR, exception.getMessage()))
        .build();
    } else if (exception instanceof ApiException) {
      return Response.status(Status.BAD_REQUEST).entity(new ApiResponse(ApiResponse.ERROR, exception.getMessage()))
        .build();
    } else {
      return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity(new ApiResponse(ApiResponse.ERROR, "a system error occured")).build();
    }
  }
}
