package eu.scape_project.watch.utils.exceptions;

/**
 * This exception is thrown when invalid parameters are supplied to the plugin.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class InvalidParameterException extends Exception {

  /**
   * serial uid.
   */
  private static final long serialVersionUID = 3368168052428596424L;

  /**
   * Default constructor.
   */
  public InvalidParameterException() {
    super();
  }

  /**
   * Message constructor.
   * 
   * @param msg
   *          the message
   */
  public InvalidParameterException(final String msg) {
    super(msg);
  }

  /**
   * {@link Throwable} constructor.
   * 
   * @param cause
   *          the cause of the exception
   */
  public InvalidParameterException(final Throwable cause) {
    super(cause);
  }

  /**
   * Message and {@link Throwable} constructor.
   * 
   * @param msg
   *          the message of the exception.
   * @param cause
   *          the cause of the exception.
   */
  public InvalidParameterException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

}
