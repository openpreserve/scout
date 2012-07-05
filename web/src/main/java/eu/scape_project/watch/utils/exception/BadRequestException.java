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
   * Create a new bad request exception.
   * 
   * @param code
   *          The HTTP code associated with this exception, usually 400.
   * @param msg
   *          A message explaining the error.
   */
  public BadRequestException(final int code, final String msg) {
    super(code, msg);
  }
}
