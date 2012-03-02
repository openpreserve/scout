package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.KBUtils;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Entity} data access object.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EntityDAO extends AbstractDAO {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EntityDAO.class);

  /**
   * The name of the relationship to {@link EntityType} in {@link Entity}.
   */
  private static final String ENTITY_TYPE_REL = KBUtils.WATCH_PREFIX + "type";

  public static String getEntityRDFId(String entityName) {
    return "<" + KBUtils.WATCH_NS + Entity.class.getSimpleName() + "/" + entityName + ">";
  }

  /**
   * Find {@link Entity} by id.
   * 
   * @param entityName
   *          the entity name
   * @return the {@link Entity} or <code>null</code> if not found
   */
  public static Entity findById(final String entityName) {
    return findById(entityName, Entity.class);
  }

  /**
   * Query for {@link Entity}.
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
   * @return A list of {@link Entity} filtered by the above constraints
   */
  public static List<Entity> query(final String bindings, final int start, final int max) {
    return query(Entity.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link Entity}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDAO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public static int count(final String bindings) {
    return count(Entity.class, bindings);
  }

  /**
   * List {@link Entity} that have the defined type.
   * 
   * @param type
   *          the {@link EntityType} related to the {@link Entity}
   * @param start
   *          the index of the first item to return
   * @param max
   *          the maximum number of items to return
   * @return a list of {@link Entity} filtered by the defined constraints
   */
  public static Collection<Entity> listWithType(final String type, final int start, final int max) {
    String bindings;

    if (StringUtils.isNotBlank(type)) {
      bindings = String.format("?s %1$s %2$s", ENTITY_TYPE_REL, EntityTypeDAO.getEntityTypeRDFId(type));
    } else {
      bindings = "";
    }
    return query(bindings, start, max);
  }
}
