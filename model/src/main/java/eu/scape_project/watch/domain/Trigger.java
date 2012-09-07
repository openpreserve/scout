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
  public Trigger(final Question question, final List<Notification> notifications, final Plan plan) {
    super();
    this.id = UUID.randomUUID().toString();
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
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.notifications == null) ? 0 : this.notifications.hashCode());
    result = prime * result + ((this.plan == null) ? 0 : this.plan.hashCode());
    result = prime * result + ((this.question == null) ? 0 : this.question.hashCode());
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
    final Trigger other = (Trigger) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    if (this.notifications == null) {
      if (other.notifications != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(notifications, other.notifications)) {
      return false;
    }
    if (this.plan == null) {
      if (other.plan != null) {
        return false;
      }
    } else if (!this.plan.equals(other.plan)) {
      return false;
    }
    if (this.question == null) {
      if (other.question != null) {
        return false;
      }
    } else if (!this.question.equals(other.question)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("Trigger(id=%1$s, plan=%2$s, question=%3$s, notifications=%4$s)", id, plan, question,
      JavaUtils.toString(notifications));
  }

}
