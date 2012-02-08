package eu.scape_project.watch.core.model;

import java.util.List;

public class PropertyValue {

  public PropertyValue() {
    super();
  }

  public PropertyValue(Property p, String v) {
    this.property = p;
    this.value = v;
  }

  private String value;

  private Property property;

  private List<Measurement> measurements;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }

  public void setMeasurements(List<Measurement> measurements) {
    this.measurements = measurements;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((property == null) ? 0 : property.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PropertyValue other = (PropertyValue) obj;
    if (property == null) {
      if (other.property != null)
        return false;
    } else if (!property.equals(other.property))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

}
