package eu.scape_project.watch.dao;

import java.util.ArrayList;
import java.util.Collection;
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

import eu.scape_project.watch.utils.KBUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.NotFoundException;
import thewebsemantic.Sparql;
import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;

/**
 * Abstract class to base Data Object classes creation.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 * @param <T>
 *          Type of the data object.
 * 
 */
public abstract class AbstractDO<T extends RdfBean<T>> {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(AbstractDO.class);

  /**
   * Find a resource by its Id.
   * 
   * @param id
   *          The id of the resource
   * @param typeClass
   *          The class of the resource we want to find
   * @return The resource or <code>null</code> if not found
   */
  protected T findById(final String id, final Class<T> typeClass) {
    try {
      return Jenabean.instance().reader().load(typeClass, id);
    } catch (final NotFoundException e) {
      return null;
    }
  }

  /**
   * Generic method to query the KB.
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
   * @param orderBy
   *          Arguments of the ORDER BY clause, e.g. DESC(?s). If null then no
   *          ORDER BY clause is added.
   * @return The list of the requested type filtered by the constraints above.
   */
  protected List<T> query(final Class<T> typeClass, final String bindings, final int start, final int max,
    final String orderBy) {

    final String classType = KBUtils.WATCH_PREFIX + typeClass.getSimpleName();

    final StringBuilder sparql = new StringBuilder();

    sparql.append(KBUtils.PREFIXES_DECL);

    sparql.append(String.format("SELECT ?s WHERE { ?s %1$s %2$s . %3$s}", KBUtils.RDF_TYPE_REL, classType, bindings));

    if (StringUtils.isNotBlank(orderBy)) {
      sparql.append(" ORDER BY ");
      sparql.append(orderBy);
    }

    LOG.trace("SPARQL:\n {}", sparql);

    final LinkedList<T> results = Sparql.exec(Jenabean.instance().reader(), typeClass, sparql.toString(),
      new QuerySolutionMap(), start, max);

    return results;
  }

  /**
   * Generic method to query the KB, using no ORDER BY.
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
  protected List<T> query(final Class<T> typeClass, final String bindings, final int start, final int max) {
    return query(typeClass, bindings, start, max, null);
  }

  /**
   * Count the number of results that a query will produce.
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
  protected int count(final Class<T> typeClass, final String bindings) {
    int count = -1;

    final String classType = KBUtils.WATCH_PREFIX + typeClass.getSimpleName();

    final StringBuilder sparql = new StringBuilder();

    sparql.append(KBUtils.PREFIXES_DECL);
    
    // TODO use Jena's SPARQL manipulation classes instead of strings
    // http://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

    sparql.append(String.format("SELECT (count(?s) as ?count) WHERE { ?s %1$s %2$s . %3$s}", KBUtils.RDF_TYPE_REL,
      classType, bindings));

    LOG.trace("SPARQL:\n {}", sparql);

    final Query query = QueryFactory.create(sparql.toString());
    final QueryExecution qexec = QueryExecutionFactory.create(query, Jenabean.instance().model());
    try {
      final ResultSet results = qexec.execSelect();
      if (results.hasNext()) {
        final QuerySolution soln = results.nextSolution();
        final Literal literal = soln.getLiteral("count");
        count = literal.getInt();
      } else {
        count = 0;
      }
    } finally {
      qexec.close();
    }

    return count;
  }

  /**
   * Save object (not deeply) and fire on update event.
   * 
   * @param object
   *          The object to create or update.
   * @return The created or updated object, the same as the input.
   */
  protected T saveImpl(final T object) {
    object.save();
    return object;
  }

  /**
   * Delete object (not cascading) and fire removed event.
   * 
   * @param object
   *          The deleted object.
   * @return The deleted object, same as the input.
   */
  public T delete(final T object) {
    object.delete();
    return object;
  }
}
