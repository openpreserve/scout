package eu.scape_project.watch.dao;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.utils.KBUtils;

/**
 * {@link Entity} data access object.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class EntityDAO extends AbstractDO<Entity> {

  /**
   * Logger.
   */
  // private static final Logger LOG = LoggerFactory.getLogger(EntityDAO.class);

  /**
   * The name of the relationship to {@link EntityType} in {@link Entity}.
   */
  private static final String ENTITY_TYPE_REL = KBUtils.WATCH_PREFIX + "type";

  /**
   * Get Entity RDF ID.
   * 
   * @param entityName
   *          The entity name that uniquely identifies this entity.
   * @return Get the RDF ID.
   */
  public static String getEntityRDFId(final String entityName) {
    return "<" + KBUtils.WATCH_NS + Entity.class.getSimpleName() + "/" + entityName + ">";
  }

  /**
   * Get Entity RDF ID.
   * 
   * @param entity
   *          The entity from which to get the RDF Id.
   * @return Get the RDF ID.
   */
  public static String getEntityRDFId(final Entity entity) {
    return getEntityRDFId(entity.getName());
  }

  /**
   * No other instances other then in {@link DAO}.
   */
  protected EntityDAO() {

  }

  /**
   * Find {@link Entity} by id.
   * 
   * @param entityName
   *          the entity name
   * @return the {@link Entity} or <code>null</code> if not found
   */
  public Entity findById(final String entityName) {
    return super.findById(entityName, Entity.class);
  }

  /**
   * Query for {@link Entity}.
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
   * @return A list of {@link Entity} filtered by the above constraints
   */
  public List<Entity> query(final String bindings, final int start, final int max) {
    return super.query(Entity.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link Entity}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(Entity.class, bindings);
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
  public Collection<Entity> listWithType(final String type, final int start, final int max) {
    String bindings;

    if (StringUtils.isNotBlank(type)) {
      bindings = String.format("?s %1$s %2$s", ENTITY_TYPE_REL, EntityTypeDAO.getEntityTypeRDFId(type));
    } else {
      bindings = "";
    }
    return this.query(bindings, start, max);
  }

  /**
   * Save a entity into the knowledge base.
   * 
   * @param entity
   *          The entity to save.
   * @return The persisted entity object.
   */
  public Entity save(final Entity entity) {
    return super.saveImpl(entity);
  }

}
