package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * Fetches the size of the smallest object within the collection.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ObjectsMinSizeCommand extends Command {

  public ObjectsMinSizeCommand(Property p) {
    this.setProperty(p);
  }

  /**
   * Retrieves the size (in bytes) within the collection.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    pv.setValue(this.getReader().getObjectsMinSize());
    pv.setProperty(this.getProperty());
    return pv;
  }
}
