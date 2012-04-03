package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * Fetches the number of objects within the collection.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ObjectsCountCommand extends Command {

  public ObjectsCountCommand(Property p) {
    this.setProperty(p);
  }

  /**
   * Retrieves the number of objects.
   * 
   * @return the value of the number count.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    pv.setValue(this.getReader().getObjectsCount());
    pv.setProperty(this.getProperty());
    return pv;
  }

}
