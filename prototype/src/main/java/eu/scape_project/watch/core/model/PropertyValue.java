package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.core.KB;

@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.PROPERTY_VALUE)
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyValue extends RdfBean<PropertyValue> {

	public static String createId(String entityName, String propertyName) {
		return entityName + "/" + propertyName;
	}

	private void updateId() {
		if (entity != null && property != null) {
			id = createId(entity.getName(), property.getName());
		}
	}

	public PropertyValue() {
		super();
	}

	public PropertyValue(Entity e, Property p, String v) {
		super();
		this.entity = e;
		this.property = p;
		this.value = v;

		updateId();
	}

	@Id
	@JsonIgnore
	private String id;

	@XmlElement
	private String value;

	@XmlElement
	@JsonProperty
	private Entity entity;

	@XmlElement
	@JsonProperty
	private Property property;

	public String getId() {
		return id;
	}

	// @XmlElement
	// @JsonProperty
	// private List<Measurement> measurements;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;

		updateId();
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	// public List<Measurement> getMeasurements() {
	// return measurements;
	// }
	//
	// public void setMeasurements(List<Measurement> measurements) {
	// this.measurements = measurements;
	// }

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;// TODO Auto-generated method stub

	// result = prime * result
	// + ((property == null) ? 0 : property.hashCode());
	// result = prime * result + ((value == null) ? 0 : value.hashCode());
	// return result;
	// }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyValue other = (PropertyValue) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
