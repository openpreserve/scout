package eu.scape_project.watch.core.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;

public class WatchClient {

	private static final String ENTITY = "entity";
	private static final String ENTITY_TYPE = "entitytype";
	private static final String PROPERTY = "property";
	private static final String PROPERTY_VALUE = "propertyvalue";

	private final WebResource resource;
	private final String format;

	public WatchClient(WebResource resource) {
		this(resource, "json");
	}

	public WatchClient(WebResource resource, String format) {
		super();
		this.resource = resource;
		this.format = format;
	}

	/***************** ENTITY ******************************/

	public Entity createEntity(Entity entity) {
		return resource.path(ENTITY + "." + format + "/")
				.accept(MediaType.APPLICATION_JSON).post(Entity.class, entity);
	}

	public Entity getEntity(String name) {
		return resource.path(ENTITY + "." + format + "/" + name)
				.accept(MediaType.APPLICATION_JSON).get(Entity.class);
	}

	public Entity updateEntity(String name, Entity entity) {
		return resource.path(ENTITY + "." + format + "/" + name)
				.accept(MediaType.APPLICATION_JSON).put(Entity.class, entity);
	}

	public List<Entity> listEntity() {
		return (List<Entity>) resource.path(ENTITY + "." + format + "/list")
				.accept(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Entity>>() {
				});
	}

	public Entity deleteEntity(String name) {
		return resource.path(ENTITY + "." + format + "/" + name)
				.accept(MediaType.APPLICATION_JSON).delete(Entity.class);
	}

	/***************** ENTITY TYPE *************************/

	public EntityType createEntityType(EntityType entitytype) {
		return resource.path(ENTITY_TYPE + "." + format + "/")
				.accept(MediaType.APPLICATION_JSON)
				.post(EntityType.class, entitytype);
	}

	public EntityType getEntityType(String name) {
		return resource.path(ENTITY_TYPE + "." + format + "/" + name)
				.accept(MediaType.APPLICATION_JSON).get(EntityType.class);
	}

	public EntityType updateEntityType(String name, EntityType entity) {
		return resource.path(ENTITY_TYPE + "." + format + "/" + name)
				.accept(MediaType.APPLICATION_JSON)
				.put(EntityType.class, entity);
	}

	public List<EntityType> listEntityType() {
		return (List<EntityType>) resource
				.path(ENTITY_TYPE + "." + format + "/list")
				.accept(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<EntityType>>() {
				});
	}

	public EntityType deleteEntityType(String name) {
		return resource.path(ENTITY_TYPE + "." + format + "/" + name)
				.accept(MediaType.APPLICATION_JSON).delete(EntityType.class);
	}

}
