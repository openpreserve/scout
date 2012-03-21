package eu.scape_project.watch.interfaces;

import java.util.List;

import eu.scape_project.watch.domain.PropertyValue;

/**
 * This is the result of a plugin. Some Plugins, such as the adaptor Plugins
 * will need specific implementations of this interface in order to provide the
 * ProeprtyValue back to the component. Others will probably not need it at all,
 * e.g. the notification plugin.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface ResultInteface {

  /**
   * Retrieves the property values.
   * @return
   */
  List<PropertyValue> getPropertyValues();
}
