package eu.scape_project.watch.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import thewebsemantic.binding.RdfBean;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exception.NotFoundException;

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
   * Special word that allows creation of a resource.
   */
  private static final String NEW = "new";

  /**
   * Special word that allows update of a resource.
   */
  private static final String UPDATE = "update";

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
   * Generic type for a List of {@link EntityType}.
   */
  private static final GenericType<List<EntityType>> ENTITYTYPE_LIST_TYPE = new GenericType<List<EntityType>>() {
  };

  /**
   * Generic type for a List of {@link Property}.
   */
  private static final GenericType<List<Property>> PROPERTY_LIST_TYPE = new GenericType<List<Property>>() {
  };

  /**
   * Generic type for a List of {@link Entity}.
   */
  private static final GenericType<List<Entity>> ENTITY_LIST_TYPE = new GenericType<List<Entity>>() {
  };

  /**
   * Generic type for a List of {@link PropertyValue}.
   */
  private static final GenericType<List<PropertyValue>> PROPERTYVALUE_LIST_TYPE = new GenericType<List<PropertyValue>>() {
  };

  /**
   * Generic type for a List of {@link PluginInfo}.
   */
  private static final GenericType<List<PluginInfo>> PLUGININFO_LIST_TYPE = new GenericType<List<PluginInfo>>() {
  };

  /**
   * Generic type for a List of {@link SourceAdaptor}.
   */
  private static final GenericType<List<SourceAdaptor>> SOURCEADAPTOR_LIST_TYPE = new GenericType<List<SourceAdaptor>>() {
  };

  /**
   * Generic type for a List of {@link Source}.
   */
  private static final GenericType<List<Source>> SOURCE_LIST_TYPE = new GenericType<List<Source>>() {
  };

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
    return this.resource.path(KBUtils.ENTITY + FS + this.format + AS + name).accept(this.format.getMediaType())
      .post(Entity.class, type);
  }

  /**
   * Get entity from server.
   * 
   * @param type
   *          the entity type
   * @param name
   *          the entity name
   * @return the entity with that name or <code>null</code> if not found
   */
  public Entity getEntity(final String type, final String name) {
    try {
      return this.resource.path(KBUtils.ENTITY + FS + this.format + AS + type + AS + name)
        .accept(this.format.getMediaType()).get(Entity.class);
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
   * @param type
   *          the type of the entity to update
   * @param name
   *          the name of the entity to update
   * @param entity
   *          the updated entity that will replace the previous one
   * @return the updated entity after merged with the knowledge base
   */
  public Entity updateEntity(final String type, final String name, final Entity entity) {
    return this.resource.path(KBUtils.ENTITY + FS + this.format + AS + type + AS + name)
      .accept(this.format.getMediaType()).put(Entity.class, entity);
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
    return (List<Entity>) this.resource.path(KBUtils.ENTITY + FS + this.format + AS + LIST)
      .queryParam(TYPE, type != null ? type : "").queryParam(START, Integer.toString(start))
      .queryParam(MAX, Integer.toString(max)).accept(this.format.getMediaType()).get(ENTITY_LIST_TYPE);
  }

  /**
   * Delete an entity.
   * 
   * @param type
   *          the type of the entity to delete.
   * @param name
   *          the name of the entity to delete.
   * @return the deleted entity.
   */
  public Entity deleteEntity(final String type, final String name) {
    return this.resource.path(KBUtils.ENTITY + FS + this.format + AS + type + AS + name)
      .accept(this.format.getMediaType()).delete(Entity.class);
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
    return this.resource.path(KBUtils.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
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
      return this.resource.path(KBUtils.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
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
    return this.resource.path(KBUtils.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
      .put(EntityType.class, entity);
  }

  /**
   * List all existing entity types.
   * 
   * @return A complete list of entity types in the KB.
   */
  public List<EntityType> listEntityType() {
    return (List<EntityType>) this.resource.path(KBUtils.ENTITY_TYPE + FS + this.format + AS + LIST)
      .accept(this.format.getMediaType()).get(ENTITYTYPE_LIST_TYPE);
  }

  /**
   * Delete an existing entity type.
   * 
   * @param name
   *          the name of the entity type to delete
   * @return the deleted entity type
   */
  public EntityType deleteEntityType(final String name) {
    return this.resource.path(KBUtils.ENTITY_TYPE + FS + this.format + AS + name).accept(this.format.getMediaType())
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
    return this.resource.path(KBUtils.PROPERTY + FS + this.format + AS + type + AS + name)
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
      return this.resource.path(KBUtils.PROPERTY + FS + this.format + AS + type + AS + name)
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
    return this.resource.path(KBUtils.PROPERTY + FS + this.format + AS + type + AS + name)
      .accept(this.format.getMediaType()).put(Property.class, property);
  }

  /**
   * List all properties in the KB.
   * 
   * @return A complete list of all properties in the KB
   */
  public List<Property> listProperty() {
    return (List<Property>) this.resource.path(KBUtils.PROPERTY + FS + this.format + AS + LIST)
      .accept(this.format.getMediaType()).get(PROPERTY_LIST_TYPE);
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
    return this.resource.path(KBUtils.PROPERTY + FS + this.format + AS + type + AS + name)
      .accept(this.format.getMediaType()).delete(Property.class);
    // TODO treat the not found exception
  }

  /**
   * Create or update a {@link PropertyValue}.
   * 
   * @param sourceAdaptorInstance
   *          The unique instance name to identify source adaptor
   * @param pv
   *          The new or updated property value.
   * @return the committed {@link PropertyValue}
   */
  public PropertyValue createPropertyValue(final String sourceAdaptorInstance, final PropertyValue pv) {
    return this.resource.path(KBUtils.PROPERTY_VALUE + FS + this.format + AS + NEW)
      .queryParam("sourceAdaptor", sourceAdaptorInstance).accept(this.format.getMediaType())
      .post(PropertyValue.class, pv);
  }

  /**
   * Get an existing {@link PropertyValue}.
   * 
   * @param entityType
   *          The {@link EntityType} related to this property value
   * @param entity
   *          The {@link Entity} related to this property value
   * @param property
   *          the {@link Property} related to this property value
   * @return the {@link PropertyValue} or <code>null</code> if not found.
   */
  public PropertyValue getPropertyValue(final String entityType, final String entity, final String property) {
    try {
      return this.resource
        .path(KBUtils.PROPERTY_VALUE + FS + this.format + AS + entityType + AS + entity + AS + property)
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
   * List all property values in the KB.
   * 
   * @return the complete list of property values in the KB
   */
  public List<PropertyValue> listPropertyValue() {
    return (List<PropertyValue>) this.resource.path(KBUtils.PROPERTY_VALUE + FS + this.format + AS + LIST)
      .accept(this.format.getMediaType()).get(PROPERTYVALUE_LIST_TYPE);
  }

  /**
   * Delete an existing property value.
   * 
   * @param entityType
   *          The {@link EntityType} related to this property value
   * @param entity
   *          The {@link Entity} related to this property value
   * @param property
   *          the {@link Property} related to this property value
   * @return the deleted property value.
   */
  public PropertyValue deletePropertyValue(final String entityType, final String entity, final String property) {
    return this.resource
      .path(KBUtils.PROPERTY_VALUE + FS + this.format + AS + entityType + AS + entity + AS + property)
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

    final Builder builder = this.resource.path(KBUtils.SYNC_REQUEST + FS + this.format + AS + target)
      .queryParam(QUERY, query).queryParam(START, Integer.toString(start)).queryParam(MAX, Integer.toString(max))
      .accept(this.format.getMediaType());

    List<? extends RdfBean<?>> ret;
    switch (target) {
      case ENTITY_TYPE:
        ret = builder.get(ENTITYTYPE_LIST_TYPE);
        break;
      case PROPERTY:
        ret = builder.get(PROPERTY_LIST_TYPE);
        break;
      case ENTITY:
        ret = builder.get(ENTITY_LIST_TYPE);
        break;
      case PROPERTY_VALUE:
        ret = builder.get(PROPERTYVALUE_LIST_TYPE);
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

  /**
   * Create a new async request.
   * 
   * @param request
   *          The new async request to insert into the KB.
   * @return The created async request after merging with the KB
   */
  public AsyncRequest createAsyncRequest(final AsyncRequest request) {
    return this.resource.path(KBUtils.ASYNC_REQUEST + FS + this.format + AS + NEW).accept(this.format.getMediaType())
      .post(AsyncRequest.class, request);
  }

  /**
   * List plug-ins by name.
   * 
   * @param name
   *          The name of the plug-ins.
   * 
   * @return A list of all plug-ins with the same name (different versions).
   */
  public List<PluginInfo> listPluginsByName(final String name) {
    return (List<PluginInfo>) this.resource.path(KBUtils.PLUGIN + FS + this.format + AS + name)
      .accept(this.format.getMediaType()).get(PLUGININFO_LIST_TYPE);
  }

  /**
   * List all plug-ins.
   * 
   * @param type
   *          Optionally filter by {@link PluginType}. Choose <code>null</code>
   *          to list all types.
   * 
   * @return A list of all plug-ins, optionally filtered by type.
   */
  public List<PluginInfo> listPlugins(final PluginType type) {
    final MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
    if (type != null) {
      queryParams.putSingle(TYPE, type.toString());
    }
    return (List<PluginInfo>) this.resource.path(KBUtils.PLUGIN + FS + this.format + AS + LIST)
      .queryParams(queryParams).accept(this.format.getMediaType()).get(PLUGININFO_LIST_TYPE);
  }

  /**
   * Get an existing {@link SourceAdaptor}.
   * 
   * @param instance
   *          Source adaptor instance unique identifier
   * @return the {@link SourceAdaptor} or <code>null</code> if not found.
   */
  public SourceAdaptor getSourceAdaptor(final String instance) {
    try {
      return this.resource.path(KBUtils.SOURCE_ADAPTOR + FS + this.format + AS + instance)
        .accept(this.format.getMediaType()).get(SourceAdaptor.class);
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
   * List all source adaptors.
   * 
   * @param active
   *          Optionally filter by active state. Choose <code>null</code> to
   *          list all.
   * 
   * @return A list of all source adaptors, optionally filtered by active state.
   */
  public List<SourceAdaptor> listSourceAdaptors(final Boolean active) {
    final MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
    if (active != null) {
      queryParams.putSingle("active", active.toString());
    }
    return (List<SourceAdaptor>) this.resource.path(KBUtils.SOURCE_ADAPTOR + FS + this.format + AS + LIST)
      .queryParams(queryParams).accept(this.format.getMediaType()).get(SOURCEADAPTOR_LIST_TYPE);
  }

  /**
   * Create and register a new source adaptor.
   * 
   * @param pluginName
   *          The related plug-in name
   * @param pluginVersion
   *          The related plug-in version
   * @param instance
   *          The adaptor instance unique identifier
   * @param sourceName
   *          The name of the related source
   * @return The created async request after merging with the KB
   */
  public SourceAdaptor createSourceAdaptor(final String pluginName, final String pluginVersion, final String instance,
    final String sourceName) {
    final MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
    queryParams.putSingle("name", pluginName);
    queryParams.putSingle("version", pluginVersion);
    queryParams.putSingle("instance", instance);
    queryParams.putSingle("source", sourceName);

    try {
      return this.resource.path(KBUtils.SOURCE_ADAPTOR + FS + this.format + AS + NEW).queryParams(queryParams)
        .accept(this.format.getMediaType()).post(SourceAdaptor.class);
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
   * Update an existing {@link SourceAdaptor}.
   * 
   * @param updatedSourceAdaptor
   *          The updated source adaptor
   * @return the committed source adaptor
   */
  public SourceAdaptor updateSourceAdaptor(final SourceAdaptor updatedSourceAdaptor) {
    return this.resource.path(KBUtils.SOURCE_ADAPTOR + FS + this.format + AS + UPDATE)
      .accept(this.format.getMediaType()).put(SourceAdaptor.class, updatedSourceAdaptor);
  }

  /**
   * Get an existing {@link Source}.
   * 
   * @param name
   *          Source name
   * @return the {@link Source} or <code>null</code> if not found.
   */
  public Source getSource(final String name) {
    try {
      return this.resource.path(KBUtils.SOURCE + FS + this.format + AS + name).accept(this.format.getMediaType())
        .get(Source.class);
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
   * List all sources.
   * 
   * @return A list of all sources.
   */
  public List<Source> listSources() {
    return (List<Source>) this.resource.path(KBUtils.SOURCE + FS + this.format + AS + LIST)
      .accept(this.format.getMediaType()).get(SOURCE_LIST_TYPE);
  }

  /**
   * Create a {@link PropertyValue}.
   * 
   * @param source
   *          The new source.
   * @return the committed {@link Source}
   */
  public Source createSource(final Source source) {
    return this.resource.path(KBUtils.SOURCE + FS + this.format + AS + NEW).accept(this.format.getMediaType())
      .post(Source.class, source);
  }

  /**
   * Update an existing {@link Source}.
   * 
   * @param updatedSource
   *          The updated source
   * @return the committed source adaptor
   */
  public Source updateSource(final Source updatedSource) {
    return this.resource.path(KBUtils.SOURCE + FS + this.format + AS + UPDATE).accept(this.format.getMediaType())
      .put(Source.class, updatedSource);
  }

  /**
   * Delete an existing source.
   * 
   * @param sourceName
   *          The source name.
   * @return the deleted source.
   */
  public Source deleteSource(final String sourceName) {
    try {
      return this.resource.path(KBUtils.SOURCE + FS + this.format + AS + sourceName).accept(this.format.getMediaType())
        .delete(Source.class);
    } catch (final UniformInterfaceException e) {
      final ClientResponse resp = e.getResponse();
      if (resp.getStatus() == NotFoundException.CODE) {
        // TODO throw exception
        return null;
      } else {
        throw e;
      }
    }
  }
}
