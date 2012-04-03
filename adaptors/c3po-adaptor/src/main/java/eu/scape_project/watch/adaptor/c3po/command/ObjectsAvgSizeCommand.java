package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * Fetches the average size of an object within the collection profile.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ObjectsAvgSizeCommand extends Command {

  public ObjectsAvgSizeCommand(Property p) {
    this.setProperty(p);
  }

  /**
   * Fetches the average size (in bytes).
   * 
   * @return the value of the average size.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    pv.setValue(this.getReader().getObjectsAvgSize());
    pv.setProperty(this.getProperty());
    return pv;
  }

}
