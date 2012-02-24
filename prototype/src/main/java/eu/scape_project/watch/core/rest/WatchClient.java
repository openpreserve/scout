package eu.scape_project.watch.core.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;
import eu.scape_project.watch.core.model.RequestTarget;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import thewebsemantic.binding.RdfBean;

/**
 * Client for Watch REST service.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class WatchClient {
  // private final Logger log = LoggerFactory.getLogger(WatchClient.class);

  /**
   * Separator between the resource and the format.
   */
  private static final String FS = ".";

  /**
   * Separator between the resource and the arguments.
   */
  private static final String AS = "/";

  /**
   * Special word that allows listing of a resource.
   */
  private static final String LIST = "list";

  /**
   * Query key to define a listing start index.
   */
  private static final String START = "start";

  /**
   * Query key to define a listing maximum number of items.
   */
  private static final String MAX = "max";

  /**
   * Query key to define a listing filtered by Entity Type.
   */
  private static final String TYPE = "type";

  /**
   * Query key to define a listing filtered by query.
   */
  private static final String QUERY = "query";

  /**
   * Jersey web resource connection.
   */
  private final WebResource resource;

  /**
   * Output format to use.
   */
  private final Format format;

  /**
   * The format of the output.
   * 
   * @author Luis Faria <lfaria@keep.pt>
   * 
   */
  public static enum Format {
    /**
     * Use JSON as output format.
     */
    JSON,
    /**
     * Use XML as output format.
     */
    XML;

    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }

    /**
     * Helper method to get related MediaType.
     * 
     * @return The related MediaType or <code>null</code> if not applicable
     */
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

  /**
   * Create a new Watch client.
   * 
   * @param resource
   *          the jersey web resource to use
   * @param format
   *          the output format to use
   */
  public WatchClient(final WebResource resource, final Format format) {
    super();
    this.resource = resource;
    this.format = format;
  }

  /**
   * Create a new {@link Entity}.
   * 
   * @param name
   *          the entity name
   * @param type
   *          the name of the entity type
   * @return the created entity
   */
  public Entity createEntity(final String name, final String type) {
    return this.resource.path(KB.ENTITY + FS + this.format + AS + name).accept(this.format.getMediaType())
      .post(Entity.class, type);
  }

  /**
   * Get entity from server.
   * 
   * @param name
   *          the entity name
   * @return the entity with that name or <code>null</code> if not found
   */
  public Entity getEntity(final String name) {
    try {
      return this.resource.path(KB.ENTITY + FS + this.format + AS + name).accept(this.format.getMediaType())
        .get(Entity.class);
    } catch (final UniformInterfaceException e) {
      final ClientResponse resp = e.getResponse();
      if (resp.getStatus() == NotFoundException.CODE) {
        return null;
      } else {
        throw e;
      }
    }
  }

  /**
   * Update the entity defined by the name.
   * 
   * @param name
   *          the name of the entity to update
   * @param entity
   *          the updated entity that will replace the previous one
   * @return the updated entity after merged with the knowledge base
   */
  public Entity updateEntity(final String name, final Entity entity) {
    return this.resource.path(KB.ENTITY + FS + this.format + AS + name).accept(this.format.getMediaType())
      .put(Entity.class, entity);
  }

  /**
   * List all entities in the knowledge base.
   * 
   * @return the complete list of entities.
   */
  public List<Entity> listEntity(int start, int max) {
    return listEntity(null, start, max);
  }

  public List<Entity> listEntity(String type, int start, int max) {
    return (List<Entity>) this.resource.path(KB.ENTITY + FS + this.format + AS + LIST)
      .queryParam(TYPE, type != null ? type : "").queryParam(START, Integer.toString(start))
      .queryParam(MAX, Integer.toString(max)).accept(this.format.getMediaType()).get(new GenericType<List<Entity>>() {
      });
  }

  /**
   * Delete an entity.
   * 
   * @param name
   *          the name of the entity to delete.
   * @return the deleted entity.
   */
  public Entity deleteEntity(final String name) {
    return this.resource.path(KB.ENTITY + FS + this.format + AS + name).accept(this.format.getMediaType())
      .delete(Entity.class);
  }

  /**
   * Create a new entity type.
   * 
   * @param name
   *          a unique name to identify this entity type
   * @param description
   *          the entity type description
   * @return the newly created entity type
   */
  public EntityType createEntityType(final String name, final String description) {
    return this.resource.path(KB.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
      .post(EntityType.class, description);
  }

  /**
   * Get an entity type.
   * 
   * @param name
   *          the entity type name
   * @return the {@link EntityType} or <code>null</code> if not found
   */
  public EntityType getEntityType(final String name) {
    try {
      return this.resource.path(KB.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
        .get(EntityType.class);
    } catch (final UniformInterfaceException e) {
      final ClientResponse resp = e.getResponse();
      if (resp.getStatus() == NotFoundException.CODE) {
        return null;
      } else {
        throw e;
      }
    }
  }

  /**
   * Update an existing entity type.
   * 
   * @param name
   *          the existing entity type name
   * @param entity
   *          the new entity type that should replace the old one
   * @return the updated entity type
   */
  public EntityType updateEntityType(final String name, final EntityType entity) {
    return this.resource.path(KB.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
      .put(EntityType.class, entity);
  }

  /**
   * List all existing entity types.
   * 
   * @return A complete list of entity types in the KB.
   */
  public List<EntityType> listEntityType() {
    return (List<EntityType>) this.resource.path(KB.ENTITY_TYPE + FS + this.format + AS + LIST)
      .accept(this.format.getMediaType()).get(new GenericType<List<EntityType>>() {
      });
  }

  /**
   * Delete an existing entity type.
   * 
   * @param name
   *          the name of the entity type to delete
   * @return the deleted entity type
   */
  public EntityType deleteEntityType(final String name) {
    return this.resource.path(KB.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
      .delete(EntityType.class);
  }

  /**
   * Create a new {@link Property}.
   * 
   * @param type
   *          the {@link EntityType} related with this property
   * @param name
   *          a unique name (within this entity type) to identity this property
   * @param description
   *          the property description
   * @return the newly created property
   */
  public Property createProperty(final String type, final String name, final String description) {
    return this.resource.path(KB.PROPERTY + FS + this.format + AS + type + AS + name)
      .accept(this.format.getMediaType()).post(Property.class, description);
  }

  /**
   * Get an existing property.
   * 
   * @param type
   *          the name of the {@link EntityType} related to this property
   * @param name
   *          the name of this property within the {@link EntityType}
   * @return the {@link Property} or <code>null</code> if not found.
   */
  public Property getProperty(final String type, final String name) {
    try {
      return this.resource.path(KB.PROPERTY + FS + this.format + AS + type + AS + name)
        .accept(this.format.getMediaType()).get(Property.class);
    } catch (final UniformInterfaceException e) {
      final ClientResponse resp = e.getResponse();
      if (resp.getStatus() == NotFoundException.CODE) {
        return null;
      } else {
        throw e;
      }
    }
  }

  /**
   * Update an existing property.
   * 
   * @param type
   *          the {@link EntityType} related to this property
   * @param name
   *          the name of this property within the {@link EntityType}
   * @param property
   *          the new update property
   * @return the updated Property after merging with the KB.
   */
  public Property updateProperty(final String type, final String name, final Property property) {
    return this.resource.path(KB.PROPERTY + FS + this.format + AS + type + AS + name)
      .accept(this.format.getMediaType()).put(Property.class, property);
  }

  /**
   * List all properties in the KB.
   * 
   * @return A complete list of all properties in the KB
   */
  public List<Property> listProperty() {
    return (List<Property>) this.resource.path(KB.PROPERTY + FS + this.format + AS + LIST)
      .accept(this.format.getMediaType()).get(new GenericType<List<Property>>() {
      });
  }

  /**
   * Delete an existing property.
   * 
   * @param type
   *          the {@link EntityType} related to this property
   * @param name
   *          the name of this property within the {@link EntityType}
   * @return The deleted property.
   */
  public Property deleteProperty(final String type, final String name) {
    return this.resource.path(KB.PROPERTY + FS + this.format + AS + type + AS + name)
      .accept(this.format.getMediaType()).delete(Property.class);
    // TODO treat the not found exception
  }

  /**
   * Create a new {@link PropertyValue}.
   * 
   * @param entity
   *          The {@link Entity} related to this property value
   * @param property
   *          the {@link Property} related to this property value
   * @param value
   *          the value of the related property for the related entity.
   * @return the newly created {@link PropertyValue}
   */
  public PropertyValue createPropertyValue(final String entity, final String property, final String value) {
    return this.resource.path(KB.PROPERTY_VALUE + FS + this.format + AS + entity + AS + property)
      .accept(this.format.getMediaType()).post(PropertyValue.class, value);
  }

  /**
   * Get an existing {@link PropertyValue}.
   * 
   * @param entity
   *          The {@link Entity} related to this property value
   * @param property
   *          the {@link Property} related to this property value
   * @return the {@link PropertyValue} or <code>null</code> if not found.
   */
  public PropertyValue getPropertyValue(final String entity, final String property) {
    try {
      return this.resource.path(KB.PROPERTY_VALUE + FS + this.format + AS + entity + AS + property)
        .accept(this.format.getMediaType()).get(PropertyValue.class);
    } catch (final UniformInterfaceException e) {
      final ClientResponse resp = e.getResponse();
      if (resp.getStatus() == NotFoundException.CODE) {
        return null;
      } else {
        throw e;
      }
    }
  }

  /**
   * Update an existing {@link PropertyValue}.
   * 
   * @param entity
   *          The {@link Entity} related to this property value
   * @param property
   *          the {@link Property} related to this property value
   * @param value
   *          The updated value of the related {@link Property} to the related
   *          {@link Entity}
   * @return the updated {@link PropertyValue}
   */
  public PropertyValue updatePropertyValue(final String entity, final String property, final String value) {
    return this.resource.path(KB.PROPERTY_VALUE + FS + this.format + AS + entity + AS + property)
      .accept(this.format.getMediaType()).put(PropertyValue.class, value);
  }

  /**
   * List all property values in the KB.
   * 
   * @return the complete list of property values in the KB
   */
  public List<PropertyValue> listPropertyValue() {
    return (List<PropertyValue>) this.resource.path(KB.PROPERTY_VALUE + FS + this.format + AS + LIST)
      .accept(this.format.getMediaType()).get(new GenericType<List<PropertyValue>>() {
      });
  }

  /**
   * Delete an existing property value.
   * 
   * @param entity
   *          The {@link Entity} related to this property value
   * @param property
   *          the {@link Property} related to this property value
   * @return the deleted property value.
   */
  public PropertyValue deletePropertyValue(final String entity, final String property) {
    return this.resource.path(KB.PROPERTY_VALUE + FS + this.format + AS + entity + AS + property)
      .accept(this.format.getMediaType()).delete(PropertyValue.class);
    // TODO treat the not found exception
  }

  /**
   * Make a synchronous request query.
   * 
   * @param <T>
   *          A class that must extends RdfBean and should be related to
   *          possible targets of a request.
   * @param targetClass
   *          The query target class, that will define the resource type.
   * @param query
   *          The SPARQL query bindings.
   * @param start
   *          The index of the first item to retrieve.
   * @param max
   *          The maximum number of items to retrieve.
   * @return The list of resources of the type defined by target, filtered by
   *         the constraints above.
   */
  @SuppressWarnings("unchecked")
  public <T extends RdfBean<T>> List<T> getRequest(final Class<T> targetClass, final String query, final int start,
    final int max) {

    final RequestTarget target = RequestTarget.getTargetByClass(targetClass);

    final Builder builder = this.resource.path(KB.SYNC_REQUEST + FS + this.format + AS + target)
      .queryParam(QUERY, query).queryParam(START, Integer.toString(start)).queryParam(MAX, Integer.toString(max))
      .accept(this.format.getMediaType());

    List<? extends RdfBean<?>> ret;
    switch (target) {
      case ENTITY_TYPE:
        ret = builder.get(new GenericType<List<EntityType>>() {
        });
        break;
      case PROPERTY:
        ret = builder.get(new GenericType<List<Property>>() {
        });
        break;
      case ENTITY:
        ret = builder.get(new GenericType<List<Entity>>() {
        });
        break;
      case PROPERTY_VALUE:
        ret = builder.get(new GenericType<List<PropertyValue>>() {
        });
        break;
      default:
        ret = null;
        break;
    }

    // return this.resource.path(KB.SYNC_REQUEST + FS + this.format + AS +
    // target).queryParam(QUERY, query)
    // .queryParam(START, Integer.toString(start)).queryParam(MAX,
    // Integer.toString(max))
    // .accept(this.format.getMediaType()).get(new
    // GenericType<List<T>>(targetClass));
    return (List<T>) ret;
  }
}
