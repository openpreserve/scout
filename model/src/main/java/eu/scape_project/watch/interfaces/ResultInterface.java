package eu.scape_project.watch.interfaces;

import eu.scape_project.watch.common.DefaultResult;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * This is the result of a plugin. Some Plugins, such as the adaptor Plugins
 * will need specific implementations of this interface in order to provide the
 * {@link PropertyValue} back to the component. Others will probably not need it at all,
 * e.g. the notification plugin. The class {@link DefaultResult} provides a
 * simple default implementation.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface ResultInterface {

  /**
   * Retrieves the entity of the current measurement.
   * 
   * @return the {@link Entity}.
   */
  Entity getEntity();

  /**
   * Retrieves the property of the current measurement.
   * 
   * @return the {@link Property}
   */
  Property getProperty();

  /**
   * Retrieves the measurement.
   * 
   * @return the {@link PropertyValue}
   */
  PropertyValue getValue();
}
