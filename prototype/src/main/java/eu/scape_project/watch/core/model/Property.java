package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.core.KB;

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
@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.PROPERTY)
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
    return entityTypeName + "/" + propertyName;
  }

  /**
   * Update property Id based on the related {@link EntityType} and property
   * name.
   */
  private void updateId() {
    if (type != null && name != null) {
      id = createId(type.getName(), name);
    }
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
   * Create a new empty property.
   */
  public Property() {
    super();
  }

  /**
   * Create a new property of type {@link DataType#TEXT}.
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
    this.datatype = DataType.TEXT;

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

  public String getId() {
    return this.id;
  }

  public EntityType getType() {
    return this.type;
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
    return this.name;
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
    return this.description;
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
  
  // TODO define hashCode()

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

}
