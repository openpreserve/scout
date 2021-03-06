package eu.scape_project.watch.dao;

import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.utils.KBUtils;

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
   * @param entityTypeId
   *          The entity type id
   * @return the complete RDF Id, using namespace prefix.
   */
  public static String getEntityTypeRDFId(final String entityTypeId) {
    return KBUtils.getRdfId(EntityType.class, entityTypeId);
  }

  /**
   * Get the {@link EntityType} complete RDF Id to use in SPARQL query.
   * 
   * @param type
   *          The entity type
   * @return the complete RDF Id, using namespace prefix.
   */
  public static String getEntityTypeRDFId(final EntityType type) {
    return getEntityTypeRDFId(type.getId());
  }

  /**
   * No other instances other then in {@link DAO}.
   */
  protected EntityTypeDAO() {

  }

  /**
   * Get {@link EntityType} by its name.
   * 
   * @param id
   *          the entity type id
   * @return The {@link EntityType} or <code>null</code> if not found
   */
  public EntityType findById(final String id) {
    return super.findById(id, EntityType.class);
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

  /**
   * Save a entity type into the knowledge base.
   * 
   * @param type
   *          The entity type to save.
   * @return The persisted entity type object.
   */
  public EntityType save(final EntityType type) {
    return super.saveImpl(type);
  }

}
