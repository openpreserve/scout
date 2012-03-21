package eu.scape_project.watch.adaptor.c3po.common;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.interfaces.ResultInterface;

public class ProfileResult implements ResultInterface {

  private List<PropertyValue> result;

  public ProfileResult() {
    this.result = new ArrayList<PropertyValue>();
  }

  public ProfileResult(List<PropertyValue> result) {
    this.result = result;
  }

  public void add(PropertyValue v) {
    this.result.add(v);
  }

  public void add(List<PropertyValue> values) {
    this.result.addAll(values);
  }

  @Override
  public List<PropertyValue> getPropertyValues() {
    return this.result;
  }

}
