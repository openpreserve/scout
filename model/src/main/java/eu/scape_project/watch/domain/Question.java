package eu.scape_project.watch.domain;

import java.util.UUID;

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
 * Questions are predefined points of interest related directly or indirectly to
 * {@link Source} and {@link Property}. The Questions can be parameterized in
 * order to offer some flexibility to the Planner.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.QUESTION)
@XmlAccessorType(XmlAccessType.FIELD)
public class Question extends RdfBean<Question> {

  /**
   * The unique identifier of the question.
   */
  @Id
  @XmlElement(required = true)
  private String id;

  /**
   * The SPARQL query to execute in the KB.
   */
  @XmlElement
  private String sparql;

  /**
   * The target resource type, to which the query result must bind to.
   */
  @XmlElement
  @JsonProperty
  private RequestTarget target;

  /**
   * Create a new empty question.
   */
  public Question() {
    super();
    this.id = UUID.randomUUID().toString();
  }

  /**
   * Create a new question.
   * 
   * @param sparql
   *          The SPARQL query to execute in the KB
   * @param target
   *          The target resource type, to which the query result must bind to.
   * @param types
   *          The entity types that are questioned
   * @param properties
   *          The properties that are questioned
   * @param entities
   *          The entities that are questioned
   * @param period
   *          The preferred period in minutes in which to re-assess this
   *          question
   * 
   */
  public Question(final String sparql, final RequestTarget target) {
    super();
    this.id = UUID.randomUUID().toString();
    this.sparql = sparql;
    this.target = target;

  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getSparql() {
    return sparql;
  }

  public void setSparql(final String sparql) {
    this.sparql = sparql;
  }

  public RequestTarget getTarget() {
    return target;
  }

  public void setTarget(final RequestTarget target) {
    this.target = target;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((sparql == null) ? 0 : sparql.hashCode());
    result = prime * result + ((target == null) ? 0 : target.hashCode());
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
    if (!(obj instanceof Question)) {
      return false;
    }
    Question other = (Question) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (sparql == null) {
      if (other.sparql != null) {
        return false;
      }
    } else if (!sparql.equals(other.sparql)) {
      return false;
    }
    if (target != other.target) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("Question [id=%s, sparql=%s, target=%s]", id, sparql, target);
  }

}
