package eu.scape_project.watch.core.dao;

import java.util.Collection;
import java.util.LinkedList;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Sparql;

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
  private static final String ENTITY_TYPE_REL = "type";

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
    final String rdfstype = KB.RDFS_NS + ENTITY_TYPE_REL;
    final String idtype = KB.WATCH_NS + Entity.class.getSimpleName();
    final String typeRlsType = KB.WATCH_NS + ENTITY_TYPE_REL;
    final String typeType = KB.WATCH_NS + EntityType.class.getSimpleName() + "/" + type;

    final String query = String.format("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
      + "SELECT ?s WHERE { ?s <%1$s> <%2$s> . ?s <%3$s> <%4$s>}", rdfstype, idtype, typeRlsType, typeType);

    LOG.debug("Query: {}", query);

    final LinkedList<Entity> results = Sparql.exec(KB.getInstance().getReader(), Entity.class, query,
      new QuerySolutionMap(), start, max);
    return results;
  }
}
