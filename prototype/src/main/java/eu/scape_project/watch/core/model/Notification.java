package eu.scape_project.watch.core.model;

import eu.scape_project.watch.core.KBUtils;
import eu.scape_project.watch.core.common.ModelUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.NOTIFICATION)
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
  @XmlElement(name = "entry")
  @XmlElementWrapper(name = "parameters")
  @JsonProperty
  private Collection<Entry> parameters;

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
  public Notification(final NotificationType type, final Collection<Entry> parameters) {
    this();
    this.setType(type);
    this.setParameters(parameters);
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
    this(type, ModelUtils.mapToEntryList(parameters));
  }

  /**
   * Get the unique Id of this notification.
   * 
   * @return the Id
   */
  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public NotificationType getType() {
    return this.type;
  }

  public void setType(final NotificationType type) {
    this.type = type;
  }

  public Collection<Entry> getParameters() {
    return this.parameters;
  }

  public Map<String, String> getParameterMap() {
    return ModelUtils.entryListToMap(this.getParameters());
  }

  public void setParameters(final Collection<Entry> parameters) {
    this.parameters = parameters;
  }

  /**
   * Set the parameters using a Map<String, String>.
   * 
   * @param parameterMap
   *          The map with the parameters
   */
  public void setParameterMap(final Map<String, String> parameterMap) {
    this.setParameters(ModelUtils.mapToEntryList(parameterMap));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    Notification other = (Notification) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (parameters == null) {
      if (other.parameters != null)
        return false;
    } else if (!parameters.equals(other.parameters))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Notification [id=" + id + ", type=" + type + ", parameters=" + parameters + "]";
  }

}
