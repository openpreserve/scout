package eu.scape_project.watch.adaptor.c3po.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * A command that fetches a distribution for the provided property.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class DistributionCommand extends Command {

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
    final List<Object> values = new ArrayList<Object>();

    for (Map.Entry<String, String> e : distribution.entrySet()) {
      values.add(new DictionaryItem(e.getKey(), e.getValue()));
    }

    pv.setValues(values);
    pv.setProperty(this.getProperty());

    return pv;
  }

}
