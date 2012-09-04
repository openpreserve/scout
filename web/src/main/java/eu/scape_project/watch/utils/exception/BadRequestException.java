package eu.scape_project.watch.utils.exception;

/**
 * Exception that is thrown when the request is not valid.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class BadRequestException extends ApiException {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * HTTP code related to this exception.
   */
  public static final int CODE = 400;

  /**
   * Create a new bad request exception.
   * 
   * @param msg
   *          A message explaining the error.
   */
  public BadRequestException(final String msg) {
    super(CODE, msg);
  }

  /**
   * Create a new bad request exception.
   * 
   * @param msg
   *          A message explaining the error.
   * @param cause
   *          The error cause
   * 
   */
  public BadRequestException(final String msg, final Throwable cause) {
    super(CODE, msg, cause);
  }
}
