package eu.scape_project.watch.components.elements;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.Property;




/**
 * Class which says to an adaptor which entity - property it needs to fetch 
 * @author kresimir
 *
 */
public class Task {

	Entity entity;
	Property property;
	
	public Task() {
		entity=null;
		property=null;
	}
	
	public Task(Entity e, Property p){
		entity=e;
		property=p;
	}

	public void setEntity(Entity e) {
		entity = e;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public void setProperty(Property p){
		property = p;
	}
	
	public Property getProperty() {
		return property;
	}
	
	@Override
	 public boolean equals(Object obj) {
		if (this==obj)
			return true;
		if (obj==null) 
			return false;
		 if (getClass() != obj.getClass())
		      return false;
		 Task tmp = (Task) obj;
		 return entity.equals(tmp.getEntity()) && property.equals(tmp.getProperty());
		
	}
}
