package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * Fetches the size of the largest object within the collection.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ObjectsMaxSizeCommand extends Command {

  public ObjectsMaxSizeCommand(Property p) {
    this.setProperty(p);
  }

  /**
   * Retrieves the size (in bytes) of the largest object in the collection.
   * 
   * @return the value of the size.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    pv.setValue(this.getReader().getObjectsMaxSize());
    pv.setProperty(this.getProperty());
    return pv;
  }

}
