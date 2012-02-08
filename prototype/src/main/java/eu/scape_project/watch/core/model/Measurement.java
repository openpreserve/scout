package eu.scape_project.watch.core.model;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.core.rest.util.exception.NotNullConstraintException;

public class Measurement {

  private static final Logger LOG = LoggerFactory.getLogger(Measurement.class);

  private PropertyValue propertyValue;

  private long timestamp;

  private SourceAdaptor adaptor;

  public Measurement() {
    super();
  }

  public Measurement(PropertyValue pv, long t) {
    this.propertyValue = pv;
    this.timestamp = t;
  }

  public PropertyValue getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(PropertyValue propertyValue) {
    this.propertyValue = propertyValue;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((adaptor == null) ? 0 : adaptor.hashCode());
    result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
    Measurement other = (Measurement) obj;
    if (adaptor == null) {
      if (other.adaptor != null)
        return false;
    } else if (!adaptor.equals(other.adaptor))
      return false;
    if (propertyValue == null) {
      if (other.propertyValue != null)
        return false;
    } else if (!propertyValue.equals(other.propertyValue))
      return false;
    if (timestamp != other.timestamp)
      return false;
    return true;
  }

  /*
   * Normally an exception in a lifecycle callback
   * should initiate a rollback. It seems however,
   * that Empire is not doing that.
   */
  //@PrePersist
  void onPrePersist() {
    LOG.debug("Prepersisting Measurement");
    if (adaptor == null) {
      throw new NotNullConstraintException("Source adaptor in measurement cannot be null");
    }

    if (propertyValue == null) {
      throw new NotNullConstraintException("Property value in measurement canno be null");
    }
    
    if (timestamp == 0L) {
      timestamp = new Date().getTime();
    }
  }

}
