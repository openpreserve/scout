package eu.scape_project.watch.core.dao;

import java.util.List;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.EntityType;

/**
 * {@link EntityType} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EntityTypeDAO extends AbstractDAO {

  /**
   * Get the {@link EntityType} complete RDF Id to use in SPARQL query.
   * 
   * @param entityTypeName
   *          The entity type name
   * @return the complete RDF Id, using namespace prefix.
   */
  public static String getEntityTypeRDFId(final String entityTypeName) {
    return "<" + KB.WATCH_NS + EntityType.class.getSimpleName() + "/" + entityTypeName + ">";
  }

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

  /**
   * Query for {@link EntityType}.
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
   * @return A list of {@link EntityType} filtered by the above constraints
   */
  public static List<EntityType> query(final String bindings, final int start, final int max) {
    return query(EntityType.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link EntityType}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDAO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public static int count(final String bindings) {
    return count(EntityType.class, bindings);
  }
}
