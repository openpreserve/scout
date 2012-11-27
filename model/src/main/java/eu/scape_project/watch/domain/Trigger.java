package eu.scape_project.watch.domain;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;

/**
 * A Trigger is a unit that contains conditions used during internal assessment
 * and {@link Notification} that are sent when the conditions are met. The
 * conditions are encoded into the {@link Question}. A trigger can also contain
 * a {@link Plan} used in external assessment.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.TRIGGER)
@XmlAccessorType(XmlAccessType.FIELD)
public class Trigger extends RdfBean<Trigger> {

  /**
   * An automatically generated unique Id.
   */
  @Id
  @XmlElement
  private String id;

  /**
   * The entity types that are questioned.
   */
  @XmlElement
  @JsonProperty
  private List<EntityType> types;

  /**
   * The properties that are questioned.
   */
  @XmlElement
  @JsonProperty
  private List<Property> properties;

  /**
   * The entities that are questioned.
   */
  @XmlElement
  @JsonProperty
  private List<Entity> entities;

  /**
   * The preferred period in milliseconds in which to re-assess this question.
   */
  @XmlElement
  private long period;

  /**
   * A plan for optional external assessment.
   */
  @XmlElement
  @JsonProperty
  private Plan plan;

  /**
   * The question that will assess if the trigger should fire.
   */
  @XmlElement
  @JsonProperty
  private Question question;

  /**
   * The notifications that should be executed in case this trigger fires.
   */
  @XmlElement
  @JsonProperty
  private List<Notification> notifications;

  /**
   * Create a new empty trigger.
   */
  public Trigger() {
    super();
    this.id = UUID.randomUUID().toString();
  }

  /**
   * Create a new trigger.
   * 
   * @param question
   *          The question that will assess if the trigger should fire.
   * @param notifications
   *          The notifications that should be executed in case this trigger
   *          fires.
   * @param plan
   *          A plan for optional external assessment, set <code>null</code> if
   *          no external assessment required.
   */
  public Trigger(final List<EntityType> types, final List<Property> properties, final List<Entity> entities,
    final long period, final Question question, final Plan plan, final List<Notification> notifications) {
    super();
    this.id = UUID.randomUUID().toString();
    this.types = types;
    this.properties = properties;
    this.entities = entities;
    this.period = period;
    this.question = question;
    this.notifications = notifications;
    this.plan = plan;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public List<EntityType> getTypes() {
    return this.types;
  }

  public void setTypes(final List<EntityType> types) {
    this.types = types;
  }

  public List<Property> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<Property> properties) {
    this.properties = properties;
  }

  public List<Entity> getEntities() {
    return entities;
  }

  public void setEntities(final List<Entity> entities) {
    this.entities = entities;
  }

  public long getPeriod() {
    return period;
  }

  public void setPeriod(final long period) {
    this.period = period;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(final Question question) {
    this.question = question;
  }

  public List<Notification> getNotifications() {
    return notifications;
  }

  public void setNotifications(final List<Notification> notifications) {
    this.notifications = notifications;
  }

  public Plan getPlan() {
    return plan;
  }

  public void setPlan(final Plan plan) {
    this.plan = plan;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entities == null) ? 0 : entities.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((notifications == null) ? 0 : notifications.hashCode());
    result = prime * result + (int) (period ^ (period >>> 32));
    result = prime * result + ((plan == null) ? 0 : plan.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((question == null) ? 0 : question.hashCode());
    result = prime * result + ((types == null) ? 0 : types.hashCode());
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
    if (!(obj instanceof Trigger)) {
      return false;
    }
    Trigger other = (Trigger) obj;
    if (entities == null) {
      if (other.entities != null) {
        return false;
      }
    } else if (!entities.equals(other.entities)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (notifications == null) {
      if (other.notifications != null) {
        return false;
      }
    } else if (!notifications.equals(other.notifications)) {
      return false;
    }
    if (period != other.period) {
      return false;
    }
    if (plan == null) {
      if (other.plan != null) {
        return false;
      }
    } else if (!plan.equals(other.plan)) {
      return false;
    }
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    } else if (!properties.equals(other.properties)) {
      return false;
    }
    if (question == null) {
      if (other.question != null) {
        return false;
      }
    } else if (!question.equals(other.question)) {
      return false;
    }
    if (types == null) {
      if (other.types != null) {
        return false;
      }
    } else if (!types.equals(other.types)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format(
      "Trigger [id=%s, types=%s, properties=%s, entities=%s, period=%s, plan=%s, question=%s, notifications=%s]", id,
      types, properties, entities, period, plan, question, notifications);
  }

}
