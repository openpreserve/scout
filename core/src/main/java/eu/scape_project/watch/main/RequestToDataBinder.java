package eu.scape_project.watch.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.Trigger;

/**
 * Class that binds async requests, and the types, entities and properties it
 * listens to, with the knowledge base change events, calling the question
 * assessment when listen data is changed.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class RequestToDataBinder {

  private class ListenerWrapper<T extends RdfBean<T>> {
    public Class<T> classToListen;
    public DOListener<T> listener;
  }

  private final AssessmentService assessmentService;

  private final Map<String, List<ListenerWrapper<? extends RdfBean<?>>>> requestToListenersMap;

  public RequestToDataBinder(final AssessmentService assessmentService) {
    this.assessmentService = assessmentService;
    requestToListenersMap = new HashMap<String, List<ListenerWrapper<? extends RdfBean<?>>>>();
  }

  public void bindRequest(final AsyncRequest request) {

    final List<ListenerWrapper<? extends RdfBean<?>>> listeners = new ArrayList<ListenerWrapper<? extends RdfBean<?>>>();

    final List<Trigger> triggers = request.getTriggers();
    for (final Trigger trigger : triggers) {

      // Event-based triggered assessment
      final List<EntityType> types = trigger.getTypes();
      final List<Entity> entities = trigger.getEntities();
      final List<Property> properties = trigger.getProperties();

      if (types != null) {
        for (final EntityType type : types) {
          final List<ListenerWrapper<? extends RdfBean<?>>> typeListeners = createListenersForType(type, trigger,
            request);
          listeners.addAll(typeListeners);
        }
      }

      if (entities != null) {
        for (Entity entity : entities) {
          if (types == null || !types.contains(entity.getType())) {
            final List<ListenerWrapper<? extends RdfBean<?>>> entityListeners = createListenersForEntity(entity,
              trigger, request);
            listeners.addAll(entityListeners);
          }
        }
      }

      if (properties != null) {
        for (Property property : properties) {
          if (types == null || !types.contains(property.getType())) {
            final List<ListenerWrapper<? extends RdfBean<?>>> propertyListeners = createListenersForProperty(property,
              trigger, request, entities);
            listeners.addAll(propertyListeners);
          }
        }
      }

    }

    // Add all listeners
    for (ListenerWrapper<? extends RdfBean<?>> listener : listeners) {
      DAO.addDOListener(listener.classToListen, listener.listener);
    }

    // Register listeners
    if (listeners.size() > 0) {
      requestToListenersMap.put(request.getId(), listeners);
    }
  }

  private List<ListenerWrapper<? extends RdfBean<?>>> createListenersForType(final EntityType type,
    final Trigger trigger, final AsyncRequest request) {

    List<ListenerWrapper<? extends RdfBean<?>>> ret = new ArrayList<RequestToDataBinder.ListenerWrapper<? extends RdfBean<?>>>();

    // Listen changes to self
    final ListenerWrapper<EntityType> typeListener = new ListenerWrapper<EntityType>();
    typeListener.classToListen = EntityType.class;
    typeListener.listener = new DOListener<EntityType>() {

      @Override
      public void onUpdated(final EntityType oType) {
        if (type.getId().equals(oType.getId())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(final EntityType oType) {
        if (type.getId().equals(oType.getId())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(typeListener);

    // Listen changes to related entities
    final ListenerWrapper<Entity> entityListener = new ListenerWrapper<Entity>();
    entityListener.classToListen = Entity.class;
    entityListener.listener = new DOListener<Entity>() {

      @Override
      public void onUpdated(Entity entity) {
        if (type.getId().equals(entity.getType().getId())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(Entity entity) {
        if (type.getId().equals(entity.getType().getId())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(entityListener);

    // Listen changes to related properties
    final ListenerWrapper<Property> propertyListener = new ListenerWrapper<Property>();
    propertyListener.classToListen = Property.class;
    propertyListener.listener = new DOListener<Property>() {

      @Override
      public void onUpdated(Property property) {
        if (type.getId().equals(property.getType().getId())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(Property property) {
        if (type.getId().equals(property.getType().getId())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(propertyListener);

    // Listen changes to related values
    final ListenerWrapper<PropertyValue> propertyValueListener = new ListenerWrapper<PropertyValue>();
    propertyValueListener.classToListen = PropertyValue.class;
    propertyValueListener.listener = new DOListener<PropertyValue>() {

      @Override
      public void onUpdated(PropertyValue pv) {
        if (type.getId().equals(pv.getEntity().getType().getId())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(PropertyValue pv) {
        if (type.getId().equals(pv.getEntity().getType().getId())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(propertyValueListener);

    return ret;
  }

  private List<ListenerWrapper<? extends RdfBean<?>>> createListenersForEntity(final Entity entity,
    final Trigger trigger, final AsyncRequest request) {
    List<ListenerWrapper<? extends RdfBean<?>>> ret = new ArrayList<RequestToDataBinder.ListenerWrapper<? extends RdfBean<?>>>();

    // Listen changes to self
    final ListenerWrapper<Entity> entityListener = new ListenerWrapper<Entity>();
    entityListener.classToListen = Entity.class;
    entityListener.listener = new DOListener<Entity>() {

      @Override
      public void onUpdated(Entity oEntity) {
        if (entity.getId().equals(oEntity.getId())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(Entity oEntity) {
        if (entity.getId().equals(oEntity.getId())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(entityListener);

    // Listen changes to values
    final ListenerWrapper<PropertyValue> valueListener = new ListenerWrapper<PropertyValue>();
    valueListener.classToListen = PropertyValue.class;
    valueListener.listener = new DOListener<PropertyValue>() {

      @Override
      public void onUpdated(PropertyValue value) {
        if (entity.getId().equals(value.getEntity().getId())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(PropertyValue value) {
        if (entity.getId().equals(value.getEntity().getId())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(valueListener);

    return ret;
  }

  private List<ListenerWrapper<? extends RdfBean<?>>> createListenersForProperty(final Property property,
    final Trigger trigger, final AsyncRequest request, final List<Entity> entities) {
    List<ListenerWrapper<? extends RdfBean<?>>> ret = new ArrayList<RequestToDataBinder.ListenerWrapper<? extends RdfBean<?>>>();

    // Listen changes to self
    final ListenerWrapper<Property> propertyListener = new ListenerWrapper<Property>();
    propertyListener.classToListen = Property.class;
    propertyListener.listener = new DOListener<Property>() {

      @Override
      public void onUpdated(final Property oProperty) {
        if (property.getId().equals(oProperty.getId())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(final Property oProperty) {
        if (property.getId().equals(oProperty.getId())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(propertyListener);

    // Listen changes to values
    final ListenerWrapper<PropertyValue> valueListener = new ListenerWrapper<PropertyValue>();
    valueListener.classToListen = PropertyValue.class;
    valueListener.listener = new DOListener<PropertyValue>() {

      @Override
      public void onUpdated(final PropertyValue value) {
        if (property.getId().equals(value.getProperty().getId()) && !entities.contains(value.getEntity())) {
          assessmentService.assess(trigger, request);
        }
      }

      @Override
      public void onRemoved(final PropertyValue value) {
        if (property.getId().equals(value.getProperty().getId()) && !entities.contains(value.getEntity())) {
          assessmentService.assess(trigger, request);
        }
      }
    };
    ret.add(valueListener);

    return ret;
  }

  public void unbindRequest(final AsyncRequest request) {
    final List<ListenerWrapper<? extends RdfBean<?>>> listeners = requestToListenersMap.get(request.getId());

    if (listeners != null) {
      for (final ListenerWrapper<? extends RdfBean<?>> listenerWrapper : listeners) {
        DAO.removeDOListener(listenerWrapper.classToListen, listenerWrapper.listener);
      }

      requestToListenersMap.remove(request.getId());
    }
  }
}
