package eu.scape_project.watch.domain;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;
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
   * A unique identifier.
   */
  @Id
  @JsonIgnore
  private String id;

  /**
   * Value holder when type is {@link String}.
   */
  @XmlElement(name = "value")
  @JsonProperty("stringValue")
  private String stringValue;

  /**
   * Value holder when type is {@link Integer}.
   */
  @XmlElement(name = "value")
  @JsonProperty("integerValue")
  private Integer integerValue;

  /**
   * Value holder when type is {@link Long}.
   */
  @XmlElement(name = "value")
  @JsonProperty("longValue")
  private Long longValue;

  /**
   * Value holder when type is {@link Float}.
   */
  @XmlElement(name = "value")
  @JsonProperty("floatValue")
  private Float floatValue;

  /**
   * Value holder when type is {@link Double}.
   */
  @XmlElement(name = "value")
  @JsonProperty("doubleValue")
  private Double doubleValue;

  /**
   * Value holder when type is {@link URI}.
   */
  @XmlElement(name = "value")
  @JsonProperty("uriValue")
  private URI uriValue;

  /**
   * Value holder when type is {@link Date}.
   */
  @XmlElement(name = "value")
  @JsonProperty("dateValue")
  private Date dateValue;
  /**
   * Value holder when type is {@link List<String>}.
   */
  @XmlElement(name = "value")
  @JsonProperty("stringListValue")
  private List<String> stringListValue = new ArrayList<String>();
  /**
   * Value holder when type is {@link List<DictionaryItem>}.
   */
  @XmlElement(name = "value")
  @JsonProperty("stringDictionaryValue")
  private List<DictionaryItem> stringDictionaryValue = new ArrayList<DictionaryItem>();

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
   * @throws InvalidJavaClassForDataTypeException
   *           Thrown if the {@link #getValue()} method does not support the
   *           {@link DataType} of the related {@link Property}.
   * @throws UnsupportedDataTypeException
   *           Thrown if the {@link DataType} of the related {@link Property} is
   *           not supported by this method.
   */
  public PropertyValue(final Entity e, final Property p, final Object v) throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    super();
    this.entity = e;
    this.property = p;
    setValue(v);

    this.updateId();
  }

  public String getId() {
    return id;
  }

  /**
   * Get Property value.
   * 
   * @return The value as an object.
   * 
   * @throws UnsupportedDataTypeException
   *           Thrown if the {@link DataType} of the related {@link Property} is
   *           not supported by this method.
   */
  @JsonProperty("value")
  public Object getValue() throws UnsupportedDataTypeException {
    
    if (property == null || property.getDatatype() == null) {
      throw new UnsupportedDataTypeException(null);
    }
    
    final DataType datatype = property.getDatatype();
    Object value;

    if (datatype.equals(DataType.STRING)) {
      value = stringValue;
    } else if (datatype.equals(DataType.INTEGER)) {
      value = integerValue;
    } else if (datatype.equals(DataType.LONG)) {
      value = longValue;
    } else if (datatype.equals(DataType.FLOAT)) {
      value = floatValue;
    } else if (datatype.equals(DataType.DOUBLE)) {
      value = doubleValue;
    } else if (datatype.equals(DataType.URI)) {
      value = uriValue;
    } else if (datatype.equals(DataType.DATE)) {
      value = dateValue;
    } else if (datatype.equals(DataType.STRING_LIST)) {
      value = stringListValue;
    } else if (datatype.equals(DataType.STRING_DICTIONARY)) {
      value = stringDictionaryValue;
    } else {
      throw new UnsupportedDataTypeException(datatype);
    }

    return value;
  }

  /**
   * Get value casted to required class.
   * 
   * @param <T>
   *          The generic type of the class to cast to.
   * @param valueClass
   *          The class to which the value should be casted to.
   * @return The casted value.
   * @throws UnsupportedDataTypeException
   *           thrown if the {@link #getValue()} method does not support the
   *           {@link DataType} of the related {@link Property}.
   */
  @SuppressWarnings("unchecked")
  public <T> T getValue(final Class<T> valueClass) throws UnsupportedDataTypeException {
    final Object value = getValue();
    return (T) value;
  }

  /**
   * Set the value of the Property.
   * 
   * @param value
   *          The property value.
   * @throws UnsupportedDataTypeException
   *           Thrown when method does not support the {@link DataType} of the
   *           related {@link Property}.
   * @throws InvalidJavaClassForDataTypeException
   *           Thrown when the Java class of the value is not compatible with
   *           the {@link DataType} defined by the class.
   */
  @SuppressWarnings("unchecked")
  public void setValue(final Object value) throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    if (property == null || property.getDatatype() == null) {
      throw new UnsupportedDataTypeException(null);
    }

    final DataType datatype = property.getDatatype();

    if (datatype.equals(DataType.STRING)) {
      if (value instanceof String) {
        stringValue = (String) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.INTEGER)) {
      if (value instanceof Integer) {
        integerValue = (Integer) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.LONG)) {
      if (value instanceof Long) {
        longValue = (Long) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.FLOAT)) {
      if (value instanceof Float) {
        floatValue = (Float) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.DOUBLE)) {
      if (value instanceof Double) {
        doubleValue = (Double) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.URI)) {
      if (value instanceof URI) {
        uriValue = (URI) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.DATE)) {
      if (value instanceof Date) {
        dateValue = (Date) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.STRING_LIST)) {
      if (value instanceof List) {
        stringListValue = (List<String>) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else if (datatype.equals(DataType.STRING_DICTIONARY)) {
      if (value instanceof List) {
        stringDictionaryValue = (List<DictionaryItem>) value;
      } else {
        throw new InvalidJavaClassForDataTypeException(value, datatype);
      }
    } else {
      throw new UnsupportedDataTypeException(datatype);
    }
  }

  /**
   * Set the value of the Property. Same as {@link #setValue(Object)} but
   * forcing type checking with the given value class.
   * 
   * @param <T>
   *          The generic type for the type check.
   * 
   * @param value
   *          The property value.
   * @param valueClass
   *          The property value Java class.
   * @throws UnsupportedDataTypeException
   *           Thrown when method does not support the {@link DataType} of the
   *           related {@link Property}.
   * @throws InvalidJavaClassForDataTypeException
   *           Thrown when the Java class of the value is not compatible with
   *           the {@link DataType} defined by the class.
   */
  public <T> void setValue(final T value, final Class<T> valueClass) throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    setValue(value);
  }

  public Entity getEntity() {
    return entity;
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
    return property;
  }

  /**
   * Set the related {@link Property} and update the Id.
   * 
   * @param property
   *          The related {@link Property}
   */
  public void setProperty(final Property property) {
    this.property = property;

    this.updateId();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
    result = prime * result + ((doubleValue == null) ? 0 : doubleValue.hashCode());
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + ((floatValue == null) ? 0 : floatValue.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((integerValue == null) ? 0 : integerValue.hashCode());
    result = prime * result + ((longValue == null) ? 0 : longValue.hashCode());
    result = prime * result + ((property == null) ? 0 : property.hashCode());
    result = prime * result + ((stringDictionaryValue == null) ? 0 : stringDictionaryValue.hashCode());
    result = prime * result + ((stringListValue == null) ? 0 : stringListValue.hashCode());
    result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
    result = prime * result + ((uriValue == null) ? 0 : uriValue.hashCode());
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
    if (dateValue == null) {
      if (other.dateValue != null) {
        return false;
      }
    } else if (!dateValue.equals(other.dateValue)) {
      return false;
    }
    if (doubleValue == null) {
      if (other.doubleValue != null) {
        return false;
      }
    } else if (!doubleValue.equals(other.doubleValue)) {
      return false;
    }
    if (entity == null) {
      if (other.entity != null) {
        return false;
      }
    } else if (!entity.equals(other.entity)) {
      return false;
    }
    if (floatValue == null) {
      if (other.floatValue != null) {
        return false;
      }
    } else if (!floatValue.equals(other.floatValue)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (integerValue == null) {
      if (other.integerValue != null) {
        return false;
      }
    } else if (!integerValue.equals(other.integerValue)) {
      return false;
    }
    if (longValue == null) {
      if (other.longValue != null) {
        return false;
      }
    } else if (!longValue.equals(other.longValue)) {
      return false;
    }
    if (property == null) {
      if (other.property != null) {
        return false;
      }
    } else if (!property.equals(other.property)) {
      return false;
    }
    if (stringDictionaryValue == null) {
      if (other.stringDictionaryValue != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(stringDictionaryValue, other.stringDictionaryValue)) {
      return false;
    }
    if (stringListValue == null) {
      if (other.stringListValue != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(stringListValue, other.stringListValue)) {
      return false;
    }
    if (stringValue == null) {
      if (other.stringValue != null) {
        return false;
      }
    } else if (!stringValue.equals(other.stringValue)) {
      return false;
    }
    if (uriValue == null) {
      if (other.uriValue != null) {
        return false;
      }
    } else if (!uriValue.equals(other.uriValue)) {
      return false;
    }

    return true;
  }

  /**
   * Update Id for new {@link Entity} or {@link Property} values.
   */
  private void updateId() {
    if (this.entity != null && this.property != null) {
      this.id = createId(this.entity.getName(), this.property.getName());
    }
  }

  @Override
  public PropertyValue save() {
    final PropertyValue propertyValue = super.save();
    DAO.fireOnUpdated(this);
    return propertyValue;
  }

  @Override
  public void delete() {
    super.delete();
    DAO.fireOnRemoved(this);
  }

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

  @Override
  public String toString() {
    try {
      return "PropertyValue [id=" + id + ", value=" + getValue() + ", valueClass=" + getValue().getClass().getName()
        + ", entity=" + entity + ", property=" + property + "]";
    } catch (UnsupportedDataTypeException e) {
      return "PropertyValue [id=" + id + ", value=ERROR:" + e.getMessage() + ", entity=" + entity + ", property="
        + property + "]";
    }
  }

}
