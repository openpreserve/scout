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
 * Questions are predefined points of interest related directly or indirectly to
 * {@link Source} and {@link Property}. The Questions can be parameterized in
 * order to offer some flexibility to the Planner.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.QUESTION)
@XmlAccessorType(XmlAccessType.FIELD)
public class Question {

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
   * @param types
   *          The entity types that are questioned
   * @param properties
   *          The properties that are questioned
   */
  public Question(final String sparql, final List<EntityType> types, final List<Property> properties) {
    super();
    this.id = UUID.randomUUID().toString();
    this.sparql = sparql;
    this.types = types;
    this.properties = properties;
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

  public String getSparql() {
    return this.sparql;
  }

  public void setSparql(final String sparql) {
    this.sparql = sparql;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((sparql == null) ? 0 : sparql.hashCode());
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
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
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
    if (types == null) {
      if (other.types != null)
        return false;
    } else if (!types.equals(other.types))
      return false;
    return true;
  }

}
