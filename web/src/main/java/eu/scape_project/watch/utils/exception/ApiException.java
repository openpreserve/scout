package eu.scape_project.watch.utils.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A generic API exception.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class ApiException extends WebApplicationException {
  /**
   * A generated serial version UID.
   */
  private static final long serialVersionUID = 1569719546889726107L;

  /**
   * The HTTP code.
   */
  private int code;

  /**
   * Create a new generic API exception.
   * 
   * @param code
   *          The HTTP code to return.
   * @param message
   *          The error message.
   */
  public ApiException(final int code, final String message) {
    super(Response.status(code).entity(message).type(MediaType.TEXT_PLAIN).build());
    this.code = code;
  }

  /**
   * Create a new generic API exception.
   * 
   * @param code
   *          The HTTP code to return.
   * @param e
   *          The error cause.
   */
  public ApiException(final int code, final Throwable e) {
    super(Response.status(code).entity(e.getClass().getSimpleName() + ": " + e.getMessage()).type(MediaType.TEXT_PLAIN)
      .build());
    this.code = code;
  }

  /**
   * Create a new generic API exception.
   * 
   * @param code
   *          The HTTP code to return.
   * @param message
   *          The error message.
   * @param e
   *          The error cause.
   */
  public ApiException(final int code, final String message, final Throwable e) {
    super(Response.status(code).entity(e.getClass().getSimpleName() + ": " + message + ", cause: " + e.getMessage())
      .type(MediaType.TEXT_PLAIN).build());
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public void setCode(final int code) {
    this.code = code;
  }
}
