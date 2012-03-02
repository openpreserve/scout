package eu.scape_project.watch.core.model;

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
 * An entity is a concrete instance of some EntityType. E.g. 'ImageMagick v1.0'
 * is a concrete instance (an entity) and has the entity type 'Action
 * component'.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.ENTITY)
@XmlAccessorType(XmlAccessType.FIELD)
public class Entity extends RdfBean<Entity> {

  /**
   * Create a new empty Entity.
   */
  public Entity() {
    super();
  }

  /**
   * Create a new Entity.
   * 
   * @param et
   *          The type of the entity
   * @param n
   *          a name that uniquely identifies the entity
   */
  public Entity(final EntityType et, final String n) {
    this.type = et;
    this.name = n;
  }

  /**
   * The unique name of the entity.
   */
  @Id
  @XmlElement(required = true)
  private String name;

  /**
   * The type of the entity.
   */
  @XmlElement
  @JsonProperty
  private EntityType type;

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public EntityType getEntityType() {
    return this.type;
  }

  public void setEntityType(final EntityType entityType) {
    this.type = entityType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
    final Entity other = (Entity) obj;
    if (this.type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!this.type.equals(other.type)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Entity(name=" + this.name + ", type=" + this.type + ")";
  }
}
