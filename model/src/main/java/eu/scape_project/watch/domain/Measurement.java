package eu.scape_project.watch.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.utils.KBUtils;

import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * Logs the moment when a property value was measured.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.MEASUREMENT)
@XmlAccessorType(XmlAccessType.FIELD)
public class Measurement extends RdfBean<Measurement> {

  // private static final Logger LOG = LoggerFactory
  // .getLogger(Measurement.class);

  /**
   * The measured property value.
   */
  @XmlElement
  @JsonProperty
  private PropertyValue propertyValue;

  /**
   * The moment in time of the measurement.
   */
  @XmlElement
  private long timestamp;

  /**
   * The source adaptor that made the measurement.
   */
  @XmlElement
  @JsonProperty
  private SourceAdaptor adaptor;

  /**
   * Create a new empty measurement.
   */
  public Measurement() {
    super();
  }

  /**
   * Create a new measurement.
   * 
   * @param pv
   *          The measured property value
   * @param t
   *          The moment in time of the measurement
   */
  public Measurement(final PropertyValue pv, final long t) {
    this.propertyValue = pv;
    this.timestamp = t;
  }

  /**
   * The unique identifier of the measurement.
   * 
   * @return The related property value name concatenated with the timestamp
   */
  @Id
  public String getId() {
    return this.propertyValue.getProperty().getName() + "-" + this.timestamp;
  }

  public PropertyValue getPropertyValue() {
    return this.propertyValue;
  }

  public void setPropertyValue(final PropertyValue propertyValue) {
    this.propertyValue = propertyValue;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.adaptor == null) ? 0 : this.adaptor.hashCode());
    result = prime * result + ((this.propertyValue == null) ? 0 : this.propertyValue.hashCode());
    result = prime * result + (int) (this.timestamp ^ (this.timestamp >>> 32));
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Measurement other = (Measurement) obj;
    if (this.adaptor == null) {
      if (other.adaptor != null) {
        return false;
      }
    } else if (!this.adaptor.equals(other.adaptor)) {
      return false;
    }
    if (this.propertyValue == null) {
      if (other.propertyValue != null) {
        return false;
      }
    } else if (!this.propertyValue.equals(other.propertyValue)) {
      return false;
    }
    if (this.timestamp != other.timestamp) {
      return false;
    }
    return true;
  }

}
