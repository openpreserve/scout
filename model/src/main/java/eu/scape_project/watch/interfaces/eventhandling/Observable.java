package eu.scape_project.watch.interfaces.eventhandling;

/**
 * A simple observable interface.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface Observable {

  /**
   * Registers an observer to this class.
   * 
   * @param scoutComponent
   *          the observer
   * @return returns true if successful, false otherwise.
   */
  boolean addObserver(ScoutComponentListener scoutComponent);

  /**
   * Removes the observer from this class.
   * 
   * @param scoutComponent
   *          the observer
   * @return true if the observer was known and successfully removed, false
   *         otherwise.
   */
  boolean removeObserver(ScoutComponentListener scoutComponent);

  /**
   * Notifies all the observers of the change event.
   * 
   * @param evt
   *          the change event.
   */
  void notifyObservers(ScoutChangeEvent evt);

  /**
   * Removes all the observers of this class.
   */
  void clear();
}
