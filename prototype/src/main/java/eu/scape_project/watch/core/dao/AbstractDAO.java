package eu.scape_project.watch.core.dao;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.core.KB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.NotFoundException;
import thewebsemantic.Sparql;
import thewebsemantic.binding.RdfBean;

/**
 * Abstract class to base Data Access Object class creation.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public abstract class AbstractDAO {
  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(AbstractDAO.class);

  /**
   * Find a resource by the value of a specific attribute.
   * 
   * @param <T>
   *          The type of the resource we want to find
   * @param attribValue
   *          value of the attribute
   * @param typeClass
   *          the class of the resource we want to retrieve
   * @param attribRdfType
   *          the RDF type of the attribute we want to filter with
   * @return The list of resources of the defined class that have the value of
   *         the specified attribute equal to the provided one
   */
  protected static <T extends RdfBean<T>> List<T> findByAttr(final String attribValue, final Class<T> typeClass,
    final String attribRdfType) {

    final String rdfstype = KB.RDFS_NS + "type";
    final String idtype = KB.WATCH_NS + typeClass.getSimpleName();
    final String attrtype = KB.WATCH_NS + attribRdfType;

    final String query = String
      .format("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
        + "SELECT ?s WHERE { ?s <%1$s> <%2$s> . ?s <%3$s> \"%4$s\"^^xsd:string}", rdfstype, idtype, attrtype,
        attribValue);

    LOG.debug("Query: {}", query);

    final LinkedList<T> results = Sparql.exec(KB.getInstance().getReader(), typeClass, query, new QuerySolutionMap(),
      0, 1);

    return results;
  }

  /**
   * Find a resource by its Id.
   * 
   * @param <T>
   *          The class of the resource we want to find
   * @param id
   *          The id of the resource
   * @param typeClass
   *          The class of the resource we want to find
   * @return The resource or <code>null</code> if not found
   */
  protected static <T extends RdfBean<T>> T findById(final String id, final Class<T> typeClass) {
    try {
      return KB.getInstance().getReader().load(typeClass, id);
    } catch (final NotFoundException e) {
      return null;
    }
  }
}
