package eu.scape_project.watch.plugin;

/**
 * This exception is thrown when an error occurs during the exection of a
 * plugin.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PluginException extends Exception {

  /**
   * serial uid.
   */
  private static final long serialVersionUID = 8494963065417906259L;

  /**
   * Default constructo.
   */
  public PluginException() {
    super();
  }

  /**
   * Message constructor.
   * 
   * @param msg
   *          the message
   */
  public PluginException(final String msg) {
    super(msg);
  }

  /**
   * {@link Throwable} constructor.
   * 
   * @param cause
   *          the cause of the exception
   */
  public PluginException(final Throwable cause) {
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
  public PluginException(final String msg, final Throwable cause) {
    super(msg, cause);
  }
  
}
