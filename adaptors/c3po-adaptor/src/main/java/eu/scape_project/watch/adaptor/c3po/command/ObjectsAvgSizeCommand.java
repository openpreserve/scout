package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

public class ObjectsAvgSizeCommand extends Command {

  public ObjectsAvgSizeCommand(Property p) {
    this.setProperty(p);
  }
  
  @Override
  public PropertyValue execute() {
    final PropertyValue pv = new PropertyValue();
    pv.setValue(this.getReader().getObjectsAvgSize());
    pv.setProperty(this.getProperty());
    return pv;
  }

}
