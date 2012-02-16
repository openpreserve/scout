package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.model.PropertyValue;

/**
 * {@link PropertyValue} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PropertyValueDAO extends AbstractDAO {

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

}
