package eu.scape_project.watch.core.dao;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.Entity;

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

  /**
   * Generic method to query the KB.
   * 
   * @param <T>
   *          The class type to return
   * 
   * @param typeClass
   *          The class of the return type we want to bind width.
   * @param bindings
   *          The bindings to use, to be injected into SELECT ?s WHERE {?s
   *          type-of \<class\> . query}. Should use ?s for the binding. Can
   *          also use OPTIONAL{}, {{...} UNION {...}} and FILTER ... >= ... &&
   *          ... \< ...
   * @param start
   *          The index of the first item to return
   * @param max
   *          The maximum number of item to return
   * @return The list of the requested type filtered by the constraints above.
   */
  protected static <T extends RdfBean<T>> List<T> query(final Class<T> typeClass, final String bindings,
    final int start, final int max) {

    final String classType = KB.WATCH_PREFIX + typeClass.getSimpleName();

    final String sparql = String.format(KB.PREFIXES_DECL + "SELECT ?s WHERE { ?s %1$s %2$s . %3$s}", KB.RDF_TYPE_REL,
      classType, bindings);

    LOG.debug("SPARQL: {}", sparql);

    final LinkedList<T> results = Sparql.exec(KB.getInstance().getReader(), typeClass, sparql, new QuerySolutionMap(),
      start, max);

    return results;
  }

  /**
   * Count the number of results that a query will produce.
   * 
   * @param <T>
   *          The class type to return
   * 
   * @param typeClass
   *          The class of the return type we want to bind width.
   * @param bindings
   *          The bindings to use, to be injected into SELECT ?s WHERE {?s
   *          type-of \<class\> . query}. Should use ?s for the binding. Can
   *          also use OPTIONAL{}, {{...} UNION {...}} and FILTER ... >= ... &&
   *          ... \< ...
   * @return The number of results expected for this query.
   */
  protected static <T extends RdfBean<T>> int count(final Class<T> typeClass, final String bindings) {
    int count = -1;

    final String classType = KB.WATCH_PREFIX + typeClass.getSimpleName();

    final String sparql = String.format(KB.XSD_PREFIX_DECL + KB.RDF_PREFIX_DECL + KB.WATCH_PREFIX_DECL
      + "SELECT count(?s) WHERE { ?s %1$s %2$s . %3$s}", KB.RDF_TYPE_REL, classType, bindings);

    final Query query = QueryFactory.create(sparql);
    final QueryExecution qexec = QueryExecutionFactory.create(query, KB.getInstance().getModel());
    try {
      final ResultSet results = qexec.execSelect();
      if (results.hasNext()) {
        final QuerySolution soln = results.nextSolution();
        final Literal literal = soln.getLiteral("s");
        count = literal.getInt();
      } else {
        count = 0;
      }
    } finally {
      qexec.close();
    }

    return count;
  }

}
