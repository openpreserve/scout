package eu.scape_project.watch.domain;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.utils.KBUtils;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

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
  public Question(final String sparql, final RequestTarget target, final List<EntityType> types,
    final List<Property> properties, final List<Entity> entities, final long period) {
    super();
    this.id = UUID.randomUUID().toString();
    this.sparql = sparql;
    this.target = target;
    this.types = types;
    this.properties = properties;
    this.entities = entities;
    this.period = period;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.entities == null) ? 0 : this.entities.hashCode());
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + (int) (this.period ^ (this.period >>> 32));
    result = prime * result + ((this.properties == null) ? 0 : this.properties.hashCode());
    result = prime * result + ((this.sparql == null) ? 0 : this.sparql.hashCode());
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.types == null) ? 0 : this.types.hashCode());
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
    final Question other = (Question) obj;
    if (this.entities == null) {
      if (other.entities != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(this.entities, other.entities)) {
      return false;
    }
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    if (this.period != other.period) {
      return false;
    }
    if (this.properties == null) {
      if (other.properties != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(this.properties, other.properties)) {
      return false;
    }
    if (this.sparql == null) {
      if (other.sparql != null) {
        return false;
      }
    } else if (!this.sparql.equals(other.sparql)) {
      return false;
    }
    if (this.target != other.target) {
      return false;
    }
    if (this.types == null) {
      if (other.types != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(this.types, other.types)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Question [id=" + this.id + ", sparql=" + this.sparql + ", target=" + this.target + ", types="
      + Arrays.toString(this.types.toArray()) + ", properties=" + Arrays.toString(this.properties.toArray())
      + ", entities=" + Arrays.toString(this.entities.toArray()) + ", period=" + this.period + "]";
  }

}
