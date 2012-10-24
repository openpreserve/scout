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
    return KBUtils.encodeId(propertyName + KBUtils.ID_SEPARATOR + timestamp.getTime());
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
   * Flag measurement as significant.
   */
  @XmlElement
  private boolean significant;

  /**
   * Flag measurement as the last measurement.
   */
  @XmlElement
  private boolean last;

  /**
   * Flag measurement as a limit measurement.
   */
  @XmlElement
  private boolean limit;

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
   *          The measured property value.
   * @param timestamp
   *          The moment in time of the measurement.
   * @param adaptor
   *          The source adaptor that took the measurement.
   */
  public Measurement(final PropertyValue pv, final Date timestamp, final SourceAdaptor adaptor) {
    this.propertyValue = pv;
    this.timestamp = (Date) timestamp.clone();
    this.last = false;
    this.limit = false;
    this.adaptor = adaptor;

    updateSignificant();
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
    return (Date) timestamp.clone();
  }

  public void setTimestamp(final Date timestamp) {
    this.timestamp = (Date) timestamp.clone();
  }

  public boolean isSignificant() {
    return significant;
  }
  
  public void setSignificant(boolean significant) {
    this.significant = significant;
  }

  private void updateSignificant() {
    this.significant = last || limit;
  }

  public boolean isLast() {
    return last;
  }

  public void setLast(boolean last) {
    this.last = last;
    updateSignificant();
  }

  public boolean isLimit() {
    return limit;
  }

  public void setLimit(boolean limit) {
    this.limit = limit;
    updateSignificant();
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
    result = prime * result + (last ? 1231 : 1237);
    result = prime * result + (limit ? 1231 : 1237);
    result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
    result = prime * result + (significant ? 1231 : 1237);
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Measurement)) {
      return false;
    }
    Measurement other = (Measurement) obj;
    if (adaptor == null) {
      if (other.adaptor != null) {
        return false;
      }
    } else if (!adaptor.equals(other.adaptor)) {
      return false;
    }
    if (last != other.last) {
      return false;
    }
    if (limit != other.limit) {
      return false;
    }
    if (propertyValue == null) {
      if (other.propertyValue != null) {
        return false;
      }
    } else if (!propertyValue.equals(other.propertyValue)) {
      return false;
    }
    if (significant != other.significant) {
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

  @Override
  public String toString() {
    return String.format("Measurement [propertyValue=%s, timestamp=%s, significant=%s, last=%s, limit=%s, adaptor=%s]",
      propertyValue, timestamp, significant, last, limit, adaptor);
  }

}
