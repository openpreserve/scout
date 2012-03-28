package eu.scape_project.watch.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.dao.AsyncRequestDAO;
import eu.scape_project.watch.utils.KBUtils;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * An Asynchronous Request, that will be kept in the KBUtils in order to be
 * monitored an acted upon.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.ASYNC_REQUEST)
@XmlAccessorType(XmlAccessType.FIELD)
public class AsyncRequest extends RdfBean<AsyncRequest> {

  /**
   * The unique id that identifies the asynchronous request.
   */
  @Id
  @XmlElement(required = true)
  private String id;

  /**
   * The list of {@link Trigger} associated with the request.
   */
  @XmlElement
  @JsonProperty
  private List<Trigger> triggers;

  /**
   * Create a new empty request with a generated Id.
   */
  public AsyncRequest() {
    super();
    this.setId(UUID.randomUUID().toString());
    triggers = new ArrayList<Trigger>();
  }

  /**
   * Create a new request with a generated Id.
   * 
   * @param triggers
   *          The list of triggers to be installed on this request
   * 
   */
  public AsyncRequest(final List<Trigger> triggers) {
    this.setId(UUID.randomUUID().toString());
    this.setTriggers(triggers);
  }

  /**
   * Get the unique Id.
   * 
   * @return the Identifier
   */
  public String getId() {
    return this.id;
  }

  /**
   * Set the unique Id.
   * 
   * @param id
   *          The Identifier
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Get the related triggers.
   * 
   * @return A list of {@link Trigger}
   */
  public List<Trigger> getTriggers() {
    return this.triggers;
  }

  /**
   * Set the list of triggers.
   * 
   * @param triggers
   *          A list of {@link Trigger}
   */
  public void setTriggers(final List<Trigger> triggers) {
    this.triggers = triggers;
  }

  public void addTrigger(Trigger t) {
    triggers.add(t);
  }

  public List<Question> getQuestion() {
    List<Question> tmp = new ArrayList();
    for (Trigger t : triggers) {
      tmp.add(t.getQuestion());
    }
    return tmp;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
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
    AsyncRequest other = (AsyncRequest) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (triggers == null) {
      if (other.triggers != null)
        return false;
    } else if (!CollectionUtils.isEqualCollection(triggers, other.triggers))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AsyncRequest(id=" + this.id + ", triggers=" + Arrays.toString(this.triggers.toArray()) + ")";
  }

  @Override
  public AsyncRequest save() {
    final AsyncRequest req = super.save();
    AsyncRequestDAO.getInstance().fireOnUpdated(this);
    return req;
  }

  @Override
  public void delete() {
    super.delete();
    AsyncRequestDAO.getInstance().fireOnRemoved(this);
  }

}
