package eu.scape_project.watch.core.model;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.core.KB;

import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;


/**
 * An Asynchronous Request, that will be kept in the KB in order to be monitored
 * an acted upon.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.ASYNC_REQUEST)
@XmlAccessorType(XmlAccessType.FIELD)
public class AsyncRequest {

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
   * Create a new request with a generated Id.
   */
  public AsyncRequest() {
    super();
    this.setId(UUID.randomUUID().toString());
  }

  /**
   * Create a new request with a defined Id.
   * 
   * @param id
   *          The unique identifier of the request
   */
  public AsyncRequest(final String id) {
    this.setId(id);
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.triggers == null) ? 0 : this.triggers.hashCode());
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
    final AsyncRequest other = (AsyncRequest) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    if (this.triggers == null) {
      if (other.triggers != null) {
        return false;
      }
    } else if (!this.triggers.equals(other.triggers)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "AsyncRequest(id=" + this.id + ", triggers=" + this.triggers + ")";
  }
}
