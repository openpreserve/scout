package eu.scape_project.watch.adaptor.pronom.common;

/**
 * A simple comunication exception.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class CommunicationException extends Exception {

  private static final long serialVersionUID = 8530688618930155015L;

  /**
   * Creates the exception;
   */
  public CommunicationException() {
    super();
  }

  /**
   * Creates the exception with the given message.
   * 
   * @param msg
   *          the message
   */
  public CommunicationException(final String msg) {
    super(msg);
  }

  /**
   * Creates the exception and wraps the throwable.
   * 
   * @param e
   *          the throwable to wrap.
   */
  public CommunicationException(final Throwable e) {
    super(e);
  }

  /**
   * Creates an exception with the given message and wraps the passed throwable.
   * 
   * @param msg
   *          the message to pass
   * @param e
   *          the throwable to pass.
   */
  public CommunicationException(final String msg, final Throwable e) {
    super(msg, e);
  }

}
