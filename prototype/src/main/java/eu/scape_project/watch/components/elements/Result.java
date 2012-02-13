package eu.scape_project.watch.components.elements;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;

public class Result {

	Entity entity;
	Property property;
	PropertyValue propertyValue;
	
	public Result() {
		entity = null;
		property = null;
		propertyValue = null;
	}
	
	public Result(Entity e, Property p, PropertyValue pv) {
		entity=e;
		property=p;
		propertyValue=pv;
	}

	public Entity getEntity() {
		return entity;
	}

	public Property getProperty() {
		return property;
	}

	public PropertyValue getPropertyValue() {
		return propertyValue;
	}
	
	@Override 
	public String toString() {
		return entity.getName()+"."+property.getName()+"="+propertyValue.getValue();
	}
	
}
