package eu.scape_project.watch.interfaces.eventhandling;

/**
 * Implement this interface and register to an {@link Observable} to be notified
 * when change eventss happen.
 * 
 * The {@link ScoutChangeEvent} will contain a source object the source of the
 * change and a message object, which can be any kind of object.
 * 
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface ScoutComponentListener {

  /**
   * Invoked when a change happens.
   * 
   * @param evt
   *          the change event carrying some further info.
   */
  void onChange(ScoutChangeEvent evt);

}
