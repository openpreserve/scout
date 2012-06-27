package eu.scape_project.watch.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.utils.KBUtils;

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
   * Get measurement Id.
   * 
   * @param propertyName
   *          The name of the related property.
   * @param timestamp
   *          The time stamp of the measurement.
   * @return The id to be used in RDF queries.
   */
  public static final String createId(final String propertyName, final Date timestamp) {
    return propertyName + KBUtils.ID_SEPARATOR + timestamp.getTime();
  }

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
  private Date timestamp;

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
   * @param timestamp
   *          The moment in time of the measurement
   */
  public Measurement(final PropertyValue pv, final Date timestamp, final SourceAdaptor adaptor) {
    this.propertyValue = pv;
    this.timestamp = timestamp;
    this.adaptor = adaptor;
  }

  /**
   * The unique identifier of the measurement.
   * 
   * @return The related property value name concatenated with the timestamp
   */
  @Id
  public String getId() {
    String propertyName;

    if (this.propertyValue != null && this.propertyValue.getProperty() != null) {
      propertyName = this.propertyValue.getProperty().getName();
    } else {
      propertyName = "unknown";
    }

    return createId(propertyName, this.timestamp);
  }

  public PropertyValue getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(final PropertyValue propertyValue) {
    this.propertyValue = propertyValue;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final Date timestamp) {
    this.timestamp = timestamp;
  }

  public SourceAdaptor getAdaptor() {
    return adaptor;
  }

  public void setAdaptor(final SourceAdaptor adaptor) {
    this.adaptor = adaptor;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((adaptor == null) ? 0 : adaptor.hashCode());
    result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
    if (adaptor == null) {
      if (other.adaptor != null) {
        return false;
      }
    } else if (!adaptor.equals(other.adaptor)) {
      return false;
    }
    if (propertyValue == null) {
      if (other.propertyValue != null) {
        return false;
      }
    } else if (!propertyValue.equals(other.propertyValue)) {
      return false;
    }
    if (timestamp == null) {
      if (other.timestamp != null) {
        return false;
      }
    } else if (!timestamp.equals(other.timestamp)) {
      return false;
    }
    return true;
  }

}
