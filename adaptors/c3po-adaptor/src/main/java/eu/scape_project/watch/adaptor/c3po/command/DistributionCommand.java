package eu.scape_project.watch.adaptor.c3po.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

public class DistributionCommand extends Command {

  private String name;

  public DistributionCommand(Property p, String name) {
    this.setProperty(p);
    this.name = name;
  }

  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    final Map<String, String> distribution = this.getReader().getDistribution(this.name);
    final List<Object> values = new ArrayList<Object>();

    for (String key : distribution.keySet()) {
      values.add(new DictionaryItem(key, distribution.get(key)));
    }

    pv.setValues(values);
    pv.setProperty(this.getProperty());

    return pv;
  }

}
