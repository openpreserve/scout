package eu.scape_project.watch.core.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.core.KB;

@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.ENTITY_TYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityType extends RdfBean<EntityType> {

	public EntityType() {
		super();
	}

	public EntityType(String n, String d) {
		super();
		this.name = n;
		this.description = d;
	}

	@Id
	@XmlElement
	private String name;

	@XmlElement
	private String description;

	// @XmlElement
	// @JsonProperty
	// private List<Property> properties;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// public List<Property> getProperties() {
	// return properties;
	// }
	//
	// public void setProperties(List<Property> properties) {
	// this.properties = properties;
	// }

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result
	// + ((description == null) ? 0 : description.hashCode());
	// result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		EntityType other = (EntityType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString() {
		return "EntityType(name=" + name + ", description=" + description + ")";
	}

}
