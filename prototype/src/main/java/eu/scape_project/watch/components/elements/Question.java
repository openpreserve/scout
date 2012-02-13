package eu.scape_project.watch.components.elements;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;



public class Question {
	private EntityType entityType;
	private Entity entity;
	private Property p;
	private long time;
	
	public Question() {}
	
	public Question(EntityType et, Entity e, Property p, long time) {
		this.entityType=et;
		this.entity=e;
		this.p = p;
		this.time = time;
	}
	
	public Property getProperty() {
		return p;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	
	public long getTime(){
		return time;
	}
}
