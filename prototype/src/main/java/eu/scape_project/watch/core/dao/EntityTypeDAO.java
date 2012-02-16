package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.model.EntityType;

/**
 * {@link EntityType} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EntityTypeDAO extends AbstractDAO {

  /**
   * Get {@link EntityType} by its name.
   * 
   * @param entityTypeName
   *          the unique entity type name
   * @return The {@link EntityType} or <code>null</code> if not found
   */
  public static EntityType findById(final String entityTypeName) {
    return findById(entityTypeName, EntityType.class);
  }
}
