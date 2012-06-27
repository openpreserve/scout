package eu.scape_project.watch.dao;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.binding.RdfBean;

/**
 * Data Access Object to access all resources.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class DAO {

  /**
   * No instances can exist.
   */
  private DAO() {

  }

  /**
   * {@link AsyncRequest} Data Access Object.
   */
  public static final EntityTypeDAO ENTITY_TYPE = new EntityTypeDAO();

  /**
   * {@link Property} Data Access Object.
   */
  public static final PropertyDAO PROPERTY = new PropertyDAO();

  /**
   * {@link Entity} Data Access Object.
   */
  public static final EntityDAO ENTITY = new EntityDAO();

  /**
   * {@link PropertyValue} Data Access Object.
   */
  public static final PropertyValueDAO PROPERTY_VALUE = new PropertyValueDAO();

  /**
   * {@link Measurement} Data Access Object.
   */
  public static final MeasurementDAO MEASUREMENT = new MeasurementDAO();

  /**
   * {@link Source} Data Access Object.
   */
  public static final SourceDAO SOURCE = new SourceDAO();

  /**
   * {@link SourceAdaptor} Data Access Object.
   */
  public static final SourceAdaptorDAO SOURCE_ADAPTOR = new SourceAdaptorDAO();

  /**
   * {@link AsyncRequest} Data Access Object.
   */
  public static final AsyncRequestDAO ASYNC_REQUEST = new AsyncRequestDAO();

  /**
   * {@link Request} Data Access Object.
   */
  public static final RequestDAO REQUEST = new RequestDAO();

  /**
   * Map of listeners for each class.
   */
  private static final Map<Class<? extends RdfBean<?>>, List<DOListener<? extends RdfBean<?>>>> listeners = new HashMap<Class<? extends RdfBean<?>>, List<DOListener<? extends RdfBean<?>>>>();

  /**
   * Add a listeners to data object events.
   * 
   * @param <T>
   *          The generic type of the resource class.
   * 
   * @param classTolisten
   *          The class for which the listener will be registered to.
   * 
   * @param listener
   *          The listener handler.
   */
  public static <T extends RdfBean<T>> void addDOListener(final Class<T> classTolisten, final DOListener<T> listener) {
    List<DOListener<? extends RdfBean<?>>> classListeners = listeners.get(classTolisten);
    if (classListeners != null) {
      classListeners.add(listener);
    } else {
      classListeners = new ArrayList<DOListener<? extends RdfBean<?>>>();
      classListeners.add(listener);
      listeners.put(classTolisten, classListeners);
    }
  }

  /**
   * Remove existing listener of data object events.
   * 
   * @param <T>
   *          The generic type of the resource class.
   * 
   * @param classTolisten
   *          The class for which the listener will be registered to.
   * 
   * @param listener
   *          The listener to remove.
   */
  public static <T extends RdfBean<T>> void removeDOListener(final Class<T> classTolisten, final DOListener<T> listener) {
    final List<DOListener<? extends RdfBean<?>>> classListeners = listeners.get(classTolisten);

    if (classListeners != null) {
      classListeners.remove(listener);

      if (classListeners.isEmpty()) {
        listeners.remove(classTolisten);
      }
    }
  }

  /**
   * Fire an on create or update event.
   * 
   * @param <T>
   *          The generic type of the resource class.
   * @param object
   *          The created or updated resource.
   */
  public static <T extends RdfBean<T>> void fireOnUpdated(final T object) {
    final List<DOListener<? extends RdfBean<?>>> classListeners = listeners.get(object.getClass());

    if (classListeners != null) {
      for (DOListener<? extends RdfBean<?>> listener : classListeners) {
        @SuppressWarnings("unchecked")
        final DOListener<T> tListener = (DOListener<T>) listener;
        tListener.onUpdated(object);
      }
    }
  }

  /**
   * Fire an on remove event.
   * 
   * @param <T>
   *          The generic type of the resource class.
   * @param object
   *          The removed object.
   */
  public static <T extends RdfBean<T>> void fireOnRemoved(final T object) {
    final List<DOListener<? extends RdfBean<?>>> classListeners = listeners.get(object.getClass());

    if (classListeners != null) {
      for (DOListener<? extends RdfBean<?>> listener : classListeners) {
        @SuppressWarnings("unchecked")
        final DOListener<T> tListener = (DOListener<T>) listener;
        tListener.onRemoved(object);
      }
    }
  }

  /**
   * Delegate object saving to the correct DAO.
   * 
   * @param <T>
   *          The type of the generic class.
   * 
   * @param object
   *          The object to create or update.
   */
  public static <T extends RdfBean<T>> void save(final T object) {

    // Due to a bug in Oracle JDK
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6548436
    // Instead of instanceof and direct cast a work-around will be used:
    // B b = B.class.cast(a) // instead of B b = (B)a;
    // B.class.isInstance(a) // instead of a instanceof b

    // if (object instanceof EntityType) {
    // ENTITY_TYPE.save((EntityType) object);
    // } else if (object instanceof Property) {
    // PROPERTY.save((Property) object);
    // } else if (object instanceof Entity) {
    // ENTITY.save((Entity) object);
    // } else if (object instanceof PropertyValue) {
    // PROPERTY_VALUE.save((PropertyValue) object);
    // } else if (object instanceof AsyncRequest) {
    // ASYNC_REQUEST.save((AsyncRequest) object);
    // } else {
    // throw new IllegalArgumentException(object.getClass().getSimpleName());
    // }

    if (EntityType.class.isInstance(object)) {
      ENTITY_TYPE.save(EntityType.class.cast(object));
    } else if (Property.class.isInstance(object)) {
      PROPERTY.save(Property.class.cast(object));
    } else if (Entity.class.isInstance(object)) {
      ENTITY.save(Entity.class.cast(object));
    } else if (PropertyValue.class.isInstance(object)) {
      PROPERTY_VALUE.save(PropertyValue.class.cast(object));
    } else if (AsyncRequest.class.isInstance(object)) {
      ASYNC_REQUEST.save(AsyncRequest.class.cast(object));
    } else {
      throw new IllegalArgumentException(object.getClass().getSimpleName());
    }

  }

  /**
   * Save several objects at the same time, calling {@link #save(RdfBean)} on
   * each.
   * 
   * @param <T>
   *          The type of the generic class.
   * @param objects
   *          The objects to save
   */
  public static <T extends RdfBean<T>> void save(final T... objects) {
    for (T object : objects) {
      save(object);
    }
  }

  /**
   * Delegate object saving to the correct DAO.
   * 
   * @param <T>
   *          The type of the generic class.
   * @param object
   *          The object to delete.
   */
  public static <T extends RdfBean<T>> void delete(final T object) {

    // Due to a bug in Oracle JDK
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6548436
    // Instead of instanceof and direct cast a work-around will be used:
    // B b = B.class.cast(a) // instead of B b = (B)a;
    // B.class.isInstance(a) // instead of a instanceof b

    // if (object instanceof EntityType) {
    // ENTITY_TYPE.delete((EntityType) object);
    // } else if (object instanceof Property) {
    // PROPERTY.delete((Property) object);
    // } else if (object instanceof Entity) {
    // ENTITY.delete((Entity) object);
    // } else if (object instanceof PropertyValue) {
    // PROPERTY_VALUE.delete((PropertyValue) object);
    // } else if (object instanceof AsyncRequest) {
    // ASYNC_REQUEST.delete((AsyncRequest) object);
    // } else {
    // throw new IllegalArgumentException(object.getClass().getSimpleName());
    // }

    if (EntityType.class.isInstance(object)) {
      ENTITY_TYPE.delete(EntityType.class.cast(object));
    } else if (Property.class.isInstance(object)) {
      PROPERTY.delete(Property.class.cast(object));
    } else if (Entity.class.isInstance(object)) {
      ENTITY.delete(Entity.class.cast(object));
    } else if (PropertyValue.class.isInstance(object)) {
      PROPERTY_VALUE.delete(PropertyValue.class.cast(object));
    } else if (AsyncRequest.class.isInstance(object)) {
      ASYNC_REQUEST.delete(AsyncRequest.class.cast(object));
    } else {
      throw new IllegalArgumentException(object.getClass().getSimpleName());
    }
  }

  /**
   * Delete several objects at the same time, calling {@link #delete(RdfBean)}
   * on each.
   * 
   * @param <T>
   *          The type of the generic class.
   * @param objects
   *          The objects to delete
   */
  public static <T extends RdfBean<T>> void delete(final T... objects) {
    for (T object : objects) {
      delete(object);
    }
  }
}
