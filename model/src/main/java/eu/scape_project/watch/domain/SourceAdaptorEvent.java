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
 * Logs an event of the source adaptor, could signalize sucess or failure.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.SOURCE_ADAPTOR_EVENT)
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdaptorEvent extends RdfBean<SourceAdaptorEvent> {

  /**
   * Get source adaptor event ID.
   * 
   * @param adaptor
   *          The related source adaptor.
   * @param timestamp
   *          The time stamp of the event.
   * @return The id to be used in RDF queries.
   */
  public static final String createId(final SourceAdaptor adaptor, final Date timestamp) {
    return KBUtils.encodeId(adaptor.getId() + KBUtils.ID_SEPARATOR + timestamp.getTime());
  }

  /**
   * The type of the event.
   */
  private SourceAdaptorEventType type;

  /**
   * The event designation or message.
   */
  @XmlElement
  @JsonProperty
  private String message;

  /**
   * If the event relative to a successful action or to a failure.
   */
  @XmlElement
  @JsonProperty
  private boolean successful;

  /**
   * The reason of the failure, if that is the case.
   */
  @XmlElement
  @JsonProperty
  private String reason;

  /**
   * The moment in time of the event.
   */
  @XmlElement
  @JsonProperty
  private Date timestamp;

  /**
   * The source adaptor that made created the event.
   */
  @XmlElement
  @JsonProperty
  private SourceAdaptor adaptor;

  /**
   * Create a new empty event.
   */
  public SourceAdaptorEvent() {
    this.timestamp = new Date();
  }

  /**
   * Convenient method for creating an event without bouding to the source
   * adaptor. This should only be used when the source adaptor is later set, as
   * in the SchedulerListenerInterface.
   * 
   * @param type
   *          The type of the event.
   * @param message
   *          A description of the event.
   */
  public SourceAdaptorEvent(SourceAdaptorEventType type, String message) {
    this.type = type;
    this.message = message;
    this.timestamp = new Date();
    this.successful = true;
  }

  /**
   * Create a new source adaptor event.
   * 
   * @param type
   *          The type of the event.
   * @param message
   *          A message describing the event.
   * @param successful
   *          <code>true</code> if the related action was a success,
   *          <code>false</code> if it was a failure.
   * @param reason
   *          underlying reason in case of failure.
   * @param timestamp
   *          The moment in time of the event.
   * @param adaptor
   *          The source adaptor that created the event.
   */
  public SourceAdaptorEvent(final SourceAdaptorEventType type, final String message, final boolean successful,
    final String reason, final Date timestamp, final SourceAdaptor adaptor) {
    this.type = type;
    this.message = message;
    this.successful = successful;
    this.reason = reason;
    this.timestamp = (Date) timestamp.clone();
    this.adaptor = adaptor;
  }

  /**
   * The unique identifier of the measurement.
   * 
   * @return The related property value name concatenated with the timestamp
   */
  @Id
  public String getId() {
    return createId(this.adaptor, this.timestamp);
  }

  public SourceAdaptorEventType getType() {
    return type;
  }

  public void setType(SourceAdaptorEventType type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public void setSuccessful(final boolean successful) {
    this.successful = successful;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  public Date getTimestamp() {
    return (Date) timestamp.clone();
  }

  public void setTimestamp(final Date timestamp) {
    this.timestamp = (Date) timestamp.clone();
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
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + (successful ? 1231 : 1237);
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    if (getClass() != obj.getClass()) {
      return false;
    }
    SourceAdaptorEvent other = (SourceAdaptorEvent) obj;
    if (adaptor == null) {
      if (other.adaptor != null) {
        return false;
      }
    } else if (!adaptor.equals(other.adaptor)) {
      return false;
    }
    if (message == null) {
      if (other.message != null) {
        return false;
      }
    } else if (!message.equals(other.message)) {
      return false;
    }
    if (reason == null) {
      if (other.reason != null) {
        return false;
      }
    } else if (!reason.equals(other.reason)) {
      return false;
    }
    if (successful != other.successful) {
      return false;
    }
    if (timestamp == null) {
      if (other.timestamp != null) {
        return false;
      }
    } else if (!timestamp.equals(other.timestamp)) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "SourceAdaptorEvent [type=" + type + ", message=" + message + ", successful=" + successful + ", reason="
      + reason + ", timestamp=" + timestamp + ", adaptor=" + adaptor + "]";
  }

}
