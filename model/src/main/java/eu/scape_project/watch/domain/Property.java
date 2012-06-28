package eu.scape_project.watch.domain;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.utils.KBUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * A Property defines a specific part of {@link Entity} in the world that have
 * the same {@link EntityType}, i.e. it represents some characteristic of these
 * Entities. It can specify a data type for its measurements.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.PROPERTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class Property extends RdfBean<Property> {

  /**
   * Get the property id based on the entity type and property name.
   * 
   * @param entityTypeName
   *          the related entity type
   * @param propertyName
   *          the property name, unique for the related entity type
   * @return The identifier
   */
  public static String createId(final String entityTypeName, final String propertyName) {
    return entityTypeName + KBUtils.ID_SEPARATOR + propertyName;
  }

  /**
   * The unique Id.
   */
  @Id
  @JsonIgnore
  private String id;

  /**
   * The related entity type.
   */
  @XmlElement
  @JsonProperty
  private EntityType type;

  /**
   * The name, unique within properties of the same type.
   */
  @XmlElement
  private String name;

  /**
   * The description.
   */
  @XmlElement
  private String description;

  /**
   * The type of data defined by this property.
   */
  @XmlElement
  @JsonProperty
  private DataType datatype;

  /**
   * Create a new empty property of the type {@link DataType#STRING}.
   */
  public Property() {
    super();

    this.datatype = DataType.STRING;
  }

  /**
   * Create a new property of type {@link DataType#STRING}.
   * 
   * @param t
   *          The related entity type.
   * @param n
   *          The name, unique within properties of the same type.
   * @param d
   *          The description of the Property.
   */
  public Property(final EntityType t, final String n, final String d) {
    super();
    this.type = t;
    this.name = n;
    this.description = d;
    this.datatype = DataType.STRING;

    this.updateId();
  }

  /**
   * Create a new property.
   * 
   * @param t
   *          The related entity type.
   * @param n
   *          The name, unique within properties of the same type.
   * @param d
   *          The description of the Property.
   * @param dt
   *          The type of data defined by this property.
   */
  public Property(final EntityType t, final String n, final String d, final DataType dt) {
    this(t, n, d);
    this.datatype = dt;
  }

  /**
   * Update property Id based on the related {@link EntityType} and property
   * name.
   */
  private void updateId() {
    if (this.type != null && this.name != null) {
      this.id = createId(this.type.getName(), this.name);
    }
  }

  public String getId() {
    return id;
  }

  public EntityType getType() {
    return type;
  }

  /**
   * Set the Entity Type and update the Id.
   * 
   * @param entityType
   *          The new related {@link EntityType}
   */
  public void setType(final EntityType entityType) {
    this.type = entityType;
    this.updateId();
  }

  public String getName() {
    return name;
  }

  /**
   * Set the name and update the Id.
   * 
   * @param name
   *          The new name
   */
  public void setName(final String name) {
    this.name = name;
    this.updateId();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public DataType getDatatype() {
    return this.datatype;
  }

  public void setDatatype(final DataType datatype) {
    this.datatype = datatype;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((datatype == null) ? 0 : datatype.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    final Property other = (Property) obj;
    if (datatype != other.datatype) {
      return false;
    }
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
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

  @Override
  public Property save() {
    final Property property = super.save();
    DAO.fireOnUpdated(this);
    return property;
  }

  @Override
  public void delete() {
    super.delete();
    DAO.fireOnRemoved(this);
  }

  @Override
  public String toString() {
    return "Property [id=" + this.id + ", type=" + this.type + ", name=" + this.name + ", description="
      + this.description + ", datatype=" + this.datatype + "]";
  }

}
