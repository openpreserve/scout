package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_OBJECTS_MAX_SIZE;

/**
 * Fetches the size of the largest object within the collection.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 * @author lfaria@keep.pt
 * 
 *         Changed at 2012-04-18: Catching new
 *         {@link PropertyValue#setValue(Object)} exceptions.
 * 
 */
public class ObjectsMaxSizeCommand extends Command {

  private final Logger LOG = LoggerFactory.getLogger(ObjectsMaxSizeCommand.class);

  /**
   * Retrieves the size (in bytes) of the largest object in the collection.
   * 
   * @return the value of the size.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    try {
      pv.setProperty(this.getProperty(CP_OBJECTS_MAX_SIZE, "The size of the larges object in the collection (in bytes)"));
      pv.setValue(this.getReader().getObjectsMaxSize());
    } catch (UnsupportedDataTypeException e) {
      LOG.error("Could not set property value", e);
    } catch (InvalidJavaClassForDataTypeException e) {
      LOG.error("Could not set property value", e);
    }
    
    return pv;
  }

}
