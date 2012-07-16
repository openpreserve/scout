package eu.scape_project.watch.common;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.interfaces.ResultInterface;

public class DefaultResult implements ResultInterface {

  private PropertyValue value;

  public DefaultResult(final PropertyValue measurement) {
    this.value = measurement;
  }

  @Override
  public Entity getEntity() {
    return this.value.getEntity();
  }

  @Override
  public Property getProperty() {
    return this.value.getProperty();
  }

  @Override
  public PropertyValue getValue() {
    return this.value;
  }

}
