package eu.scape_project.watch.adaptor.c3po.command;

import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_COLLECTION_SIZE;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command that fetches the overall collection size of a profile.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 * @author lfaria@keep.pt
 * 
 *         Changed at 2012-04-18: Catching new
 *         {@link PropertyValue#setValue(Object)} exceptions.
 * 
 * 
 */
public class CollectionSizeCommand extends Command {

  private final Logger LOG = LoggerFactory.getLogger(CollectionSizeCommand.class);

  /**
   * Fetches the overall size (in bytes) of the collection.
   * 
   * @return the value of the property.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    try {
      pv.setProperty(this.getProperty(CP_COLLECTION_SIZE, "The overall size of the collection (in bytes)"));
      pv.setValue(this.getReader().getCollectionSize(), String.class);
    } catch (UnsupportedDataTypeException e) {
      LOG.error("Could not set property value", e);
    } catch (InvalidJavaClassForDataTypeException e) {
      LOG.error("Could not set property value", e);
    }
    
    return pv;
  }

}
