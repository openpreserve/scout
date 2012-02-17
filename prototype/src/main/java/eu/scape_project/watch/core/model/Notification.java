package eu.scape_project.watch.core.model;

import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.core.KB;

import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * A Notification describes what should happen when a {@link Trigger} is fired.
 * Each Notification has a type, e.g. Email, Push Notification, etc.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.NOTIFICATION)
@XmlAccessorType(XmlAccessType.FIELD)
public class Notification extends RdfBean<Notification> {

  /**
   * The unique id that identifies the notification.
   */
  @Id
  @XmlElement(required = true)
  private String id;

  /**
   * The type of the notification.
   */
  @XmlElement
  @JsonProperty
  private NotificationType type;

  /**
   * The notification parameters, e.g. email recipients, subject, etc.
   */
  @XmlElement
  @JsonProperty
  private Map<String, String> parameters;

  /**
   * Create a new empty notification.
   */
  public Notification() {
    super();
    this.id = UUID.randomUUID().toString();
  }

  /**
   * Create a new notification.
   * 
   * @param type
   *          The type of the notification.
   * @param parameters
   *          The notification parameters, e.g. email recipients, subject, etc.
   */
  public Notification(final NotificationType type, final Map<String, String> parameters) {
    this();
    this.setType(type);
    this.setParameters(parameters);
  }

  /**
   * Get the unique Id of this notification.
   * 
   * @return the Id
   */
  public String getId() {
    return this.id;
  }

  public NotificationType getType() {
    return this.type;
  }

  public void setType(final NotificationType type) {
    this.type = type;
  }

  public Map<String, String> getParameters() {
    return this.parameters;
  }

  public void setParameters(final Map<String, String> parameters) {
    this.parameters = parameters;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    final Notification other = (Notification) obj;
    if (this.parameters == null) {
      if (other.parameters != null) {
        return false;
      }
    } else if (!this.parameters.equals(other.parameters)) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    return true;
  }

}
