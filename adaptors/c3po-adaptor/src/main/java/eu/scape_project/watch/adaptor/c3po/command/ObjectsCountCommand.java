package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_OBJECTS_COUNT;

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

  /**
   * Default Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ObjectsCountCommand.class);

  /**
   * Retrieves the number of objects.
   * 
   * @return the value of the number count.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    try {
      pv.setProperty(this.getProperty(CP_OBJECTS_COUNT, "The overall number of objects in the collection"));
      pv.setValue(this.getReader().getObjectsCount(), String.class);

    } catch (final UnsupportedDataTypeException e) {
      LOG.error("Data type is not supported. Could not set property value", e);
    } catch (final InvalidJavaClassForDataTypeException e) {
      LOG.error("Invalid Java Class. Could not set property value", e);
    }

    return pv;
  }

}
