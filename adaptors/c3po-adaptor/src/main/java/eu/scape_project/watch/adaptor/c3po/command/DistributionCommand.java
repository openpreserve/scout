package eu.scape_project.watch.adaptor.c3po.command;

import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_DISTRIBUTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * A command that fetches a distribution for the provided property.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 * 
 * 
 * @author lfaria@keep.pt
 * 
 *         Changed at 2012-04-18: Catching new
 *         {@link PropertyValue#setValue(Object)} exceptions.
 * 
 */
public class DistributionCommand extends Command {

  /**
   * Default Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DistributionCommand.class);

  /**
   * The corresponding name of the property in the profile.
   */
  private String name;

  /**
   * Initializes the command.
   * 
   * @param name
   *          the name of the property in the profile.
   */
  public DistributionCommand(final String name) {
    this.name = name;
  }

  /**
   * Fetches the distribution and returns a {@link PropertyValue}. The
   * implementation follows the data structure of the provided property and uses
   * {@link DictionaryItem} objects to map the distribution values.
   * 
   * @return the distribution.
   */
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    final Property property = this.getProperty(String.format(CP_DISTRIBUTION, this.name),
      String.format("The %s distribution of the objects", this.name));

    final Map<String, String> distribution = this.getReader().getDistribution(this.name);
    final List<DictionaryItem> values = new ArrayList<DictionaryItem>();

    for (Map.Entry<String, String> e : distribution.entrySet()) {
      values.add(new DictionaryItem(e.getKey(), e.getValue()));
    }
    
    Collections.sort(values, new Comparator<DictionaryItem>() {

      @Override
      public int compare(DictionaryItem item1, DictionaryItem item2) {
        return item1.getValue().compareTo(item2.getValue()); //descending
      }
      
    });

    try {
      property.setDatatype(DataType.STRING_DICTIONARY);
      pv.setProperty(property);
      pv.setValue(values, List.class);

    } catch (final UnsupportedDataTypeException e) {
      LOG.error("Data type is not supported. Could not set property value", e);
    } catch (final InvalidJavaClassForDataTypeException e) {
      LOG.error("Invalid Java Class. Could not set property value", e);
    }

    return pv;
  }

}
