package eu.scape_project.watch.domain;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.utils.KBUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
   * @param n
   *          the entity type unique name
   * @param d
   *          a description
   */
  public EntityType(final String n, final String d) {
    super();
    this.name = n;
    this.description = d;
  }

  /**
   * Unique name that identifies the entity type.
   */
  @Id
  @XmlElement
  private String name;

  /**
   * A description of the entity type.
   */
  @XmlElement
  private String description;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
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
    final EntityType other = (EntityType) obj;
    if (this.description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!this.description.equals(other.description)) {
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
  public String toString() {
    return "EntityType(name=" + this.name + ", description=" + this.description + ")";
  }

}
