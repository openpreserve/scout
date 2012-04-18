package eu.scape_project.watch.adaptor.c3po.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * Fetches the number of objects within the collection.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 * @author lfaria@keep.pt
 * 
 *         Changed at 2012-04-18: Catching new
 *         {@link PropertyValue#setValue(Object)} exceptions.
 * 
 */
public class ObjectsCountCommand extends Command {

  private final Logger LOG = LoggerFactory.getLogger(ObjectsCountCommand.class);

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
    pv.setProperty(this.getProperty());
    try {
      pv.setValue(this.getReader().getObjectsCount(), String.class);
    } catch (UnsupportedDataTypeException e) {
      LOG.error("Could not set property value", e);
    } catch (InvalidJavaClassForDataTypeException e) {
      LOG.error("Could not set property value", e);
    }
    
    return pv;
  }

}
