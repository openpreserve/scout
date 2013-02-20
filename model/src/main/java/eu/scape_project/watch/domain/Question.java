package eu.scape_project.watch.domain;

import java.util.ArrayList;
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
   * Initial bindings for the sparql query.
   */
  private List<QueryBinding> bindings = new ArrayList<QueryBinding>();

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
   * @param bindings
   *          Initial bindings for the sparql query
   * @param target
   *          The target resource type, to which the query result must bind to.
   * 
   */
  public Question(final String sparql, final List<QueryBinding> bindings, final RequestTarget target) {
    super();
    this.id = UUID.randomUUID().toString();
    this.sparql = sparql;
    this.bindings = bindings;
    this.target = target;

  }

  /**
   * Convenience method to create a new question with empty set of query
   * bindings.
   * 
   * @param sparql
   *          The SPARQL query to execute in the KB
   * @param target
   *          The target resource type, to which the query result must bind to.
   */
  public Question(final String sparql, final RequestTarget target) {
    this(sparql, new ArrayList<QueryBinding>(), target);
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

  public List<QueryBinding> getBindings() {
    return bindings;
  }

  public void setBindings(List<QueryBinding> bindings) {
    if (bindings != null) {
      this.bindings = bindings;
    } else {
      this.bindings = new ArrayList<QueryBinding>();
    }
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
    result = prime * result + ((bindings == null) ? 0 : bindings.hashCode());
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
    if (bindings == null) {
      if (other.bindings != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(bindings, other.bindings)) {
      return false;
    }
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
    return String.format("Question [id=%s, sparql=%s, bindings=%s, target=%s]", id, sparql, bindings, target);
  }

}
