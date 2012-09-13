package eu.scape_project.watch.domain;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.ModelUtils;


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
  private String type;

  /**
   * The notification parameters, e.g. email recipients, subject, etc.
   */
  @XmlElement(name = "entry")
  @XmlElementWrapper(name = "parameters")
  @JsonProperty
  private List<DictionaryItem> parameters;

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
  public Notification(final String type, final List<DictionaryItem> parameters) {
    this();
    this.type = type;
    this.parameters = parameters;
  }

  /**
   * Create a new notification.
   * 
   * @param type
   *          The type of the notification.
   * @param parameters
   *          The notification parameters, e.g. email recipients, subject, etc.
   */
  public Notification(final String type, final Map<String, String> parameters) {
    this(type, ModelUtils.mapToEntryList(parameters));
  }

  /**
   * Get the unique Id of this notification.
   * 
   * @return the Id
   */
  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public List<DictionaryItem> getParameters() {
    return parameters;
  }

  public Map<String, String> getParameterMap() {
    return ModelUtils.entryListToMap(this.getParameters());
  }

  public void setParameters(final List<DictionaryItem> parameters) {
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
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    for (DictionaryItem entry : this.parameters) {
      result = prime * result + ((entry == null) ? 0 : entry.hashCode());
    }

    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
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
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    if (this.parameters == null) {
      if (other.parameters != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(this.parameters, other.parameters)) {
      return false;
    }
    if (this.type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }
  
  @Override
  public Notification save() {
    final Notification notification = super.save();
    DAO.fireOnUpdated(this);
    return notification;
  }

  @Override
  public void delete() {
    super.delete();
    DAO.fireOnRemoved(this);
  }

  @Override
  public String toString() {
    return "Notification [id=" + this.id + ", type=" + this.type + ", parameters="
      + JavaUtils.toString(this.parameters) + "]";
  }

}
