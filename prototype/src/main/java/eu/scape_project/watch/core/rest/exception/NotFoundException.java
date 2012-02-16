package eu.scape_project.watch.core.rest.exception;

/**
 * Exception thrown when a element is not found in the requested resource.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class NotFoundException extends ApiException {

  /**
   * Generated serial.
   */
  private static final long serialVersionUID = -8902097610254093563L;

  /**
   * HTTP code related to this exception.
   */
  public static final int CODE = 404;

  /**
   * Create a new not found exception.
   * 
   * @param msg
   *          a message explaining the occurrence
   */
  public NotFoundException(final String msg) {
    super(CODE, msg);
  }
}
