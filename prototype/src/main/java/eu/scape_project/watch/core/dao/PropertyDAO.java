package eu.scape_project.watch.core.dao;

import java.util.Collection;
import java.util.LinkedList;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Sparql;

/**
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PropertyDAO extends AbstractDAO {

  /**
   * The name of the relationship to {@link EntityType} in {@link Property}.
   */
  private static final String ENTITY_TYPE_REL = "type";

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(PropertyDAO.class);

  /**
   * Find a {@link Property} by the related {@link EntityType} and name.
   * 
   * @param entityTypeName
   *          The name of the related {@link EntityType}
   * @param name
   *          the name of the {@link Property}
   * @return the {@link Property} or <code>null</code> if not found
   */
  public static Property findByEntityTypeAndName(final String entityTypeName, final String name) {
    final String id = Property.createId(entityTypeName, name);
    return findById(id, Property.class);
  }

  /**
   * List properties of a type.
   * 
   * @param type
   *          The related {@link EntityType}
   * @param start
   *          the index of the first item to retrieve
   * @param max
   *          the maximum number of items to retrieve
   * @return The list of properties filtered by the above constraints
   */
  public static Collection<Property> listWithType(final String type, final int start, final int max) {
    final String rdfstype = KB.RDFS_NS + ENTITY_TYPE_REL;
    final String idtype = KB.WATCH_NS + Property.class.getSimpleName();
    final String typeRlsType = KB.WATCH_NS + ENTITY_TYPE_REL;
    final String typeType = KB.WATCH_NS + EntityType.class.getSimpleName() + "/" + type;

    final String query = String.format("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
      + "SELECT ?s WHERE { ?s <%1$s> <%2$s> . ?s <%3$s> <%4$s>}", rdfstype, idtype, typeRlsType, typeType);

    LOG.debug("Query: {}", query);

    final LinkedList<Property> results = Sparql.exec(KB.getInstance().getReader(), Property.class, query,
      new QuerySolutionMap(), start, max);
    return results;
  }

}
