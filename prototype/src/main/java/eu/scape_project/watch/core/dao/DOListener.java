package eu.scape_project.watch.core.dao;

import thewebsemantic.binding.RdfBean;

/**
 * Handler for Data Object events.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 * @param <T>
 *          The type of the data object
 */
public interface DOListener<T extends RdfBean<T>> {

  /**
   * Event fired on data object creation or update.
   * 
   * @param object
   *          The created or updated object
   */
  void onUpdated(final T object);

  /**
   * Event fired on data object removal.
   * 
   * @param object
   *          The removed data object.
   */
  void onRemoved(final T object);

}
