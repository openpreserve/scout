package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;

import java.util.Collection;
import java.util.List;

/**
 * {@link PropertyValue} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PropertyValueDAO extends AbstractDAO {

  /**
   * The name of the relationship to {@link Entity} in {@link PropertyValue}.
   */
  private static final String ENTITY_REL = KB.WATCH_PREFIX + "entity";

  /**
   * The name of the relationship to {@link Property} in {@link PropertyValue}.
   */
  private static final String PROPERTY_REL = KB.WATCH_PREFIX + "property";

  /**
   * Find {@link PropertyValue} by the related {@link Entity} and
   * {@link Property}.
   * 
   * @param entityName
   *          The name of the related {@link Entity}
   * @param propertyName
   *          The name of the related {@link Property}
   * @return The {@link PropertyValue} or <code>null</code> if not found
   */
  public static PropertyValue findByEntityAndName(final String entityName, final String propertyName) {
    final String id = PropertyValue.createId(entityName, propertyName);
    return findById(id, PropertyValue.class);
  }

  /**
   * Query for {@link PropertyValue}.
   * 
   * @see #query(Class, String, int, int)
   * 
   * @param bindings
   *          The query bindings, see
   *          {@link AbstractDAO#query(Class, String, int, int)}
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link PropertyValue} filtered by the above constraints
   */
  public static List<PropertyValue> query(final String bindings, final int start, final int max) {
    return query(PropertyValue.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link PropertyValue}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDAO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public static int count(final String bindings) {
    return count(PropertyValue.class, bindings);
  }

  /**
   * List all property values of a specific {@link Entity}.
   * 
   * @param entityName
   *          The name of the {@link Entity}
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of {@link PropertyValue} filtered by the above constraints
   */
  public static Collection<PropertyValue> listWithEntity(final String entityName, final int start, final int max) {
    final String bindings = String.format("?s %1$s %2$s", ENTITY_REL, EntityDAO.getEntityRDFId(entityName));
    return query(bindings, start, max);
  }

  /**
   * List all property values of a specific {@link Entity} and {@link Property}.
   * 
   * @param entityName
   *          The name of the {@link Entity}.
   * @param entityType
   *          The name of the {@link EntityType} to which the {@link Property}
   *          belongs to
   * @param propertyName
   *          The name of the {@link Property}.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of {@link PropertyValue} filtered by the above constraints
   */
  public static Collection<PropertyValue> listWithEntityAndProperty(final String entityName, final String entityType,
    final String propertyName, final int start, final int max) {
    final String bindings = String.format("?s %1$s %2$s . ?s %3$s %4$s", ENTITY_REL,
      EntityDAO.getEntityRDFId(entityName), PROPERTY_REL, PropertyDAO.getPropertyRDFId(entityType, propertyName));
    return query(bindings, start, max);
  }

  /**
   * List all property values of a {@link Property} independently of the
   * {@link Entity}.
   * 
   * @param entityType
   *          The name of the {@link EntityType} to which the {@link Property}
   *          belongs to
   * @param propertyName
   *          The name of the {@link Property}.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of {@link PropertyValue} filtered by the above constraints
   */
  public static Collection<PropertyValue> listWithProperty(final String entityType, final String propertyName,
    final int start, final int max) {
    final String bindings = String.format("?s %3$s %4$s", PROPERTY_REL,
      PropertyDAO.getPropertyRDFId(entityType, propertyName));
    return query(bindings, start, max);
  }

}
