package eu.scape_project.watch.interfaces.eventhandling;

/**
 * A simple scout change event that has a source and a message.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ScoutChangeEvent {

  /**
   * The source of the message or the component/object that was observed and
   * changed in other words.
   */
  private Object source;

  /**
   * The actual message. This can be any kind of object ant it is in the
   * responsibility of the publisher and subscriber to agree on the content.
   */
  private Object message;

  public ScoutChangeEvent(final Object s, final Object m) {
    this.setSource(s);
    this.setMessage(m);
  }

  public Object getSource() {
    return source;
  }

  public void setSource(Object source) {
    this.source = source;
  }

  public Object getMessage() {
    return message;
  }

  public void setMessage(Object message) {
    this.message = message;
  }
}
