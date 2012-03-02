package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.core.KBUtils;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * Each Property Value is the atomic measurement of a {@link Property} for an
 * {@link Entity} measured in a specific point in time. The moment in time is
 * defined by a related {@link Measurement}
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.PROPERTY_VALUE)
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyValue extends RdfBean<PropertyValue> {

  /**
   * Create a unique Id based on the related {@link Entity} name and related
   * {@link Property} name.
   * 
   * @param entityName
   *          The related {@link Entity} name.
   * @param propertyName
   *          The related {@link Property} name.
   * @return The {@link PropertyValue} unique Id.
   */
  public static String createId(final String entityName, final String propertyName) {
    return entityName + "/" + propertyName;
  }

  /**
   * Update Id for new {@link Entity} or {@link Property} values.
   */
  private void updateId() {
    if (entity != null && property != null) {
      id = createId(entity.getName(), property.getName());
    }
  }

  /**
   * Create a new empty property value.
   */
  public PropertyValue() {
    super();
  }

  /**
   * Create a new property value.
   * 
   * @param e
   *          The related {@link Entity}.
   * @param p
   *          The related {@link Property}.
   * @param v
   *          The value of the {@link Property} for the {@link Entity}
   */
  public PropertyValue(final Entity e, final Property p, final String v) {
    super();
    this.entity = e;
    this.property = p;
    this.value = v;

    this.updateId();
  }

  /**
   * A unique identifier.
   */
  @Id
  @JsonIgnore
  private String id;

  /**
   * The value of the related {@link Property} for the related {@link Entity}.
   */
  @XmlElement
  private String value;

  /**
   * The related {@link Entity}.
   */
  @XmlElement
  @JsonProperty
  private Entity entity;

  /**
   * The {@link Property} being measured.
   */
  @XmlElement
  @JsonProperty
  private Property property;

  public String getId() {
    return this.id;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public Entity getEntity() {
    return this.entity;
  }

  /**
   * Set the related {@link Entity} and update the Id.
   * 
   * @param entity
   *          The related {@link Entity}
   */
  public void setEntity(final Entity entity) {
    this.entity = entity;

    this.updateId();
  }

  public Property getProperty() {
    return this.property;
  }

  public void setProperty(final Property property) {
    this.property = property;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;

    // TODO Auto-generated method stub

    result = prime * result + ((property == null) ? 0 : property.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    final PropertyValue other = (PropertyValue) obj;
    if (this.property == null) {
      if (other.property != null) {
        return false;
      }
    } else if (!this.property.equals(other.property)) {
      return false;
    }
    if (this.value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
