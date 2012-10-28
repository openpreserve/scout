package eu.scape_project.watch.domain;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.utils.KBUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * An entity type describes the type of an instance. It groups instances of the
 * same type and helps the Planner to pose meaningful Watch Requests. Some
 * examples are: Format, PreservationAction, Experiment, etc.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.ENTITY_TYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityType extends RdfBean<EntityType> {

  /**
   * Create a new empty entity type.
   */
  public EntityType() {
    super();
  }

  /**
   * Create an entity type.
   * 
   * @param name
   *          the entity type unique name
   * @param description
   *          a description
   */
  public EntityType(final String name, final String description) {
    super();
    this.name = name;
    this.description = description;

    updateId();
  }

  /**
   * The unique id generated as an hash of the name.
   */
  @Id
  @XmlElement
  private String id;

  /**
   * Unique name that identifies the entity type.
   */
  @XmlElement
  private String name;

  /**
   * A description of the entity type.
   */
  @XmlElement
  private String description;

  /**
   * Update the id by hashing the name.
   */
  private void updateId() {
    this.id = KBUtils.hashId(getName());
  }

  public String getId() {
    return id;
  }

  /**
   * Set the id method for use in serialization.
   * 
   * @param id
   *          The value of ID.
   */
  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  /**
   * Set the name and update the id.
   * 
   * @param name
   *          The entity type name.
   */
  public void setName(final String name) {
    this.name = name;
    updateId();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public EntityType save() {
    final EntityType type = super.save();
    DAO.fireOnUpdated(this);
    return type;
  }

  @Override
  public void delete() {
    super.delete();
    DAO.fireOnRemoved(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    if (!(obj instanceof EntityType)) {
      return false;
    }
    final EntityType other = (EntityType) obj;
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("EntityType [id=%s, name=%s, description=%s]", id, name, description);
  }

}
