package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * A command that fetches the overall collection size of a profile.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class CollectionSizeCommand extends Command {

  /**
   * Initializes the command by setting the property of interest.
   * 
   * @param p
   *          the property of interest.
   */
  public CollectionSizeCommand(final Property p) {
    this.setProperty(p);
  }

  /**
   * Fetches the overall size (in bytes) of the collection.
   * 
   * @return the value of the property.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    pv.setValue(this.getReader().getCollectionSize());
    pv.setProperty(this.getProperty());
    return pv;
  }

}
