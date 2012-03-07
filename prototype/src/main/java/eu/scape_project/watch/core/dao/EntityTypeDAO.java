package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.KBUtils;
import eu.scape_project.watch.core.model.EntityType;

import java.util.List;

/**
 * {@link EntityType} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class EntityTypeDAO extends AbstractDO<EntityType> {

  /**
   * Get the {@link EntityType} complete RDF Id to use in SPARQL query.
   * 
   * @param entityTypeName
   *          The entity type name
   * @return the complete RDF Id, using namespace prefix.
   */
  public static String getEntityTypeRDFId(final String entityTypeName) {
    return "<" + KBUtils.WATCH_NS + EntityType.class.getSimpleName() + "/" + entityTypeName + ">";
  }

  /**
   * Singleton instance.
   */
  private static EntityTypeDAO instance = null;

  /**
   * Get singleton instance.
   * 
   * @return The singleton instance
   */
  public static EntityTypeDAO getInstance() {
    if (instance == null) {
      instance = new EntityTypeDAO();
    }
    return instance;
  }

  /**
   * No other instances can exist as this is a singleton.
   */
  private EntityTypeDAO() {

  }

  /**
   * Get {@link EntityType} by its name.
   * 
   * @param entityTypeName
   *          the unique entity type name
   * @return The {@link EntityType} or <code>null</code> if not found
   */
  public EntityType findById(final String entityTypeName) {
    return super.findById(entityTypeName, EntityType.class);
  }

  /**
   * Query for {@link EntityType}.
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
   * @return A list of {@link EntityType} filtered by the above constraints
   */
  public List<EntityType> query(final String bindings, final int start, final int max) {
    return super.query(EntityType.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link EntityType}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(EntityType.class, bindings);
  }
}
