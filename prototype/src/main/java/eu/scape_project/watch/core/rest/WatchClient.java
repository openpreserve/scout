package eu.scape_project.watch.core.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;

public class WatchClient {

	private static final String ENTITY = "entity";
	private static final String ENTITY_TYPE = "entitytype";
	private static final String PROPERTY = "property";
	private static final String PROPERTY_VALUE = "propertyvalue";

	private final WebResource resource;
	private final Format format;

	public static enum Format {
		JSON, XML;

		public String toString() {
			return super.toString().toLowerCase();
		}

		public String getMediaType() {
			if (this.equals(JSON)) {
				return MediaType.APPLICATION_JSON;
			} else if (this.equals(XML)) {
				return MediaType.APPLICATION_XML;
			} else {
				return null;
			}
		}
	}

	// public WatchClient(WebResource resource) {
	// this(resource, Format.JSON);
	// }

	public WatchClient(WebResource resource, Format format) {
		super();
		this.resource = resource;
		this.format = format;
	}

	/***************** ENTITY ******************************/

	public Entity createEntity(String name, String type) {
		return resource.path(ENTITY + "." + format + "/" + name)
				.accept(format.getMediaType()).post(Entity.class, type);
	}

	public Entity getEntity(String name) {
		try {
			return resource.path(ENTITY + "." + format + "/" + name)
					.accept(format.getMediaType()).get(Entity.class);
		} catch (UniformInterfaceException e) {
			ClientResponse resp = e.getResponse();
			if (resp.getStatus() == 404) {
				return null;
			} else {
				throw e;
			}
		}
	}

	public Entity updateEntity(String name, Entity entity) {
		return resource.path(ENTITY + "." + format + "/" + name)
				.accept(format.getMediaType()).put(Entity.class, entity);
	}

	public List<Entity> listEntity() {
		return (List<Entity>) resource.path(ENTITY + "." + format + "/list")
				.accept(format.getMediaType())
				.get(new GenericType<List<Entity>>() {
				});
	}

	public Entity deleteEntity(String name) {
		return resource.path(ENTITY + "." + format + "/" + name)
				.accept(format.getMediaType()).delete(Entity.class);
	}

	/***************** ENTITY TYPE *************************/

	public EntityType createEntityType(String name, String description) {
		return resource.path(ENTITY_TYPE + "." + format + "/" + name)
				.accept(format.getMediaType())
				.post(EntityType.class, description);
	}

	public EntityType getEntityType(String name) {
		try {
			return resource.path(ENTITY_TYPE + "." + format + "/" + name)
					.accept(format.getMediaType()).get(EntityType.class);
		} catch (UniformInterfaceException e) {
			ClientResponse resp = e.getResponse();
			if (resp.getStatus() == 404) {
				return null;
			} else {
				throw e;
			}
		}
	}

	public EntityType updateEntityType(String name, EntityType entity) {
		return resource.path(ENTITY_TYPE + "." + format + "/" + name)
				.accept(format.getMediaType()).put(EntityType.class, entity);
	}

	public List<EntityType> listEntityType() {
		return (List<EntityType>) resource
				.path(ENTITY_TYPE + "." + format + "/list")
				.accept(format.getMediaType())
				.get(new GenericType<List<EntityType>>() {
				});
	}

	public EntityType deleteEntityType(String name) {
		return resource.path(ENTITY_TYPE + "." + format + "/" + name)
				.accept(format.getMediaType()).delete(EntityType.class);
	}

}
