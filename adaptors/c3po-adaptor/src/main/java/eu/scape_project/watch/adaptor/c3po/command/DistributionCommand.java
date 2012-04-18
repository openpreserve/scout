package eu.scape_project.watch.adaptor.c3po.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private final Logger LOG = LoggerFactory.getLogger(DistributionCommand.class);

  /**
   * The corresponding name of the property in the profile.
   */
  private String name;

  /**
   * Initializes the command.
   * 
   * @param p
   *          the property.
   * @param name
   *          the name of the property in the profile.
   */
  public DistributionCommand(final Property p, final String name) {
    this.setProperty(p);
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
    final Map<String, String> distribution = this.getReader().getDistribution(this.name);
    final List<DictionaryItem> values = new ArrayList<DictionaryItem>();

    for (Map.Entry<String, String> e : distribution.entrySet()) {
      values.add(new DictionaryItem(e.getKey(), e.getValue()));
    }

    pv.setProperty(this.getProperty());
    try {
      pv.setValue(values, List.class);
    } catch (UnsupportedDataTypeException e) {
      LOG.error("Could not set property value", e);
    } catch (InvalidJavaClassForDataTypeException e) {
      LOG.error("Could not set property value", e);
    }

    return pv;
  }

}
