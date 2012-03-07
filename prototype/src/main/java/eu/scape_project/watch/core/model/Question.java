package eu.scape_project.watch.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.core.KBUtils;
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
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getSparql() {
    return this.sparql;
  }

  public void setSparql(final String sparql) {
    this.sparql = sparql;
  }

  public RequestTarget getTarget() {
    return this.target;
  }

  public void setTarget(final RequestTarget target) {
    this.target = target;
  }

  public List<Entity> getEntities() {
    return this.entities;
  }

  public void setEntities(final List<Entity> entities) {
    this.entities = entities;
  }

  public long getPeriod() {
    return this.period;
  }

  public void setPeriod(final long period) {
    this.period = period;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entities == null) ? 0 : entities.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + (int) (period ^ (period >>> 32));
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((sparql == null) ? 0 : sparql.hashCode());
    result = prime * result + ((target == null) ? 0 : target.hashCode());
    result = prime * result + ((types == null) ? 0 : types.hashCode());
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
    Question other = (Question) obj;
    if (entities == null) {
      if (other.entities != null)
        return false;
    } else if (!entities.equals(other.entities))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (period != other.period)
      return false;
    if (properties == null) {
      if (other.properties != null)
        return false;
    } else if (!properties.equals(other.properties))
      return false;
    if (sparql == null) {
      if (other.sparql != null)
        return false;
    } else if (!sparql.equals(other.sparql))
      return false;
    if (target != other.target)
      return false;
    if (types == null) {
      if (other.types != null)
        return false;
    } else if (!types.equals(other.types))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Question [id=" + id + ", sparql=" + sparql + ", target=" + target + ", types=" + types + ", properties="
      + properties + ", entities=" + entities + ", period=" + period + "]";
  }

  /*
   * THESE METHODS ARE GOING TO BE REIMPLEMENTED OR COMPLETELY REMOVED AT THE
   * MOMENT SYSTEM SUPPORTS ONLY ONE ENTITYTYPE - ENTITY PER QUESTION
   */
  public EntityType getEntityType() {
    return this.types.get(0);
  }

  public Entity getEntity() {
    return this.entities.get(0);
  }

  public Property getProperty() {
    return this.properties.get(0);
  }

  public Question(String sparql, EntityType et, Entity e, Property p, long per) {
    this.sparql = sparql;
    this.types = new ArrayList<EntityType>();
    this.types.add(et);
    this.entities = new ArrayList<Entity>();
    this.entities.add(e);
    this.properties = new ArrayList<Property>();
    this.properties.add(p);
    this.period = per;
  }
}
