package eu.scape_project.watch.dao;

import java.util.Collection;
import java.util.List;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.KBUtils;

/**
 * {@link PropertyValue} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PropertyValueDAO extends AbstractDO<PropertyValue> {

  /**
   * The name of the relationship to {@link Entity} in {@link PropertyValue}.
   */
  private static final String ENTITY_REL = KBUtils.WATCH_PREFIX + "entity";

  /**
   * The name of the relationship to {@link Property} in {@link PropertyValue}.
   */
  private static final String PROPERTY_REL = KBUtils.WATCH_PREFIX + "property";

  /**
   * Singleton instance.
   */
  private static PropertyValueDAO instance = null;

  /**
   * Get singleton instance.
   * 
   * @return The singleton instance
   */
  public static PropertyValueDAO getInstance() {
    if (instance == null) {
      instance = new PropertyValueDAO();
    }
    return instance;
  }

  /**
   * No other instances can exist as this is a singleton.
   */
  private PropertyValueDAO() {

  }

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
  public PropertyValue findByEntityAndName(final String entityName, final String propertyName) {
    final String id = PropertyValue.createId(entityName, propertyName);
    return super.findById(id, PropertyValue.class);
  }

  /**
   * Query for {@link PropertyValue}.
   * 
   * @see #query(Class, String, int, int)
   * 
   * @param bindings
   *          The query bindings, see
   *          {@link AbstractDO#query(Class, String, int, int)}
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link PropertyValue} filtered by the above constraints
   */
  public List<PropertyValue> query(final String bindings, final int start, final int max) {
    return super.query(PropertyValue.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link PropertyValue}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(PropertyValue.class, bindings);
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
  public Collection<PropertyValue> listWithEntity(final String entityName, final int start, final int max) {
    final String bindings = String.format("?s %1$s %2$s", ENTITY_REL, EntityDAO.getEntityRDFId(entityName));
    return this.query(bindings, start, max);
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
  public Collection<PropertyValue> listWithEntityAndProperty(final String entityName, final String entityType,
    final String propertyName, final int start, final int max) {
    final String bindings = String.format("?s %1$s %2$s . ?s %3$s %4$s", ENTITY_REL,
      EntityDAO.getEntityRDFId(entityName), PROPERTY_REL, PropertyDAO.getPropertyRDFId(entityType, propertyName));
    return this.query(bindings, start, max);
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
  public Collection<PropertyValue> listWithProperty(final String entityType, final String propertyName,
    final int start, final int max) {
    final String bindings = String.format("?s %1$s %2$s", PROPERTY_REL,
      PropertyDAO.getPropertyRDFId(entityType, propertyName));
    return this.query(bindings, start, max);
  }

}
