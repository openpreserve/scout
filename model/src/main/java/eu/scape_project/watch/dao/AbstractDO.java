package eu.scape_project.watch.dao;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.NotFoundException;
import thewebsemantic.Sparql;
import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;

import eu.scape_project.watch.utils.KBUtils;

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

    final String classType = KBUtils.WATCH_NS + typeClass.getSimpleName();

    final StringBuilder sparql = new StringBuilder();

    sparql.append(KBUtils.PREFIXES_DECL);

    if (StringUtils.isNotBlank(bindings)) {
      sparql.append(String.format("SELECT ?s WHERE { ?s ?rel ?class . %1$s}", bindings));
    } else {
      sparql.append("SELECT ?s WHERE { ?s ?rel ?class }");
    }

    if (StringUtils.isNotBlank(orderBy)) {
      sparql.append(" ORDER BY ");
      sparql.append(orderBy);
    }

    final Model model = Jenabean.instance().model();
    final QuerySolutionMap initialBinding = new QuerySolutionMap();
    initialBinding.add("rel", model.createProperty(KBUtils.RDF_TYPE_REL));
    initialBinding.add("class", model.createResource(classType));

    LinkedList<T> results;

    try {
      results = Sparql.exec(Jenabean.instance().reader(), typeClass, sparql.toString(), initialBinding, start, max);
      LOG.trace("The following query gave {} results:\n{} ", new Object[] {results.size(), sparql});
    } catch (final QueryParseException e) {
      LOG.error("Error parsing query: " + sparql, e);
      throw e;
    }

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
  protected synchronized int count(final Class<T> typeClass, final String bindings) {
    int count = -1;

    final String classType = KBUtils.WATCH_NS + typeClass.getSimpleName();

    final StringBuilder sparql = new StringBuilder();

    sparql.append(KBUtils.PREFIXES_DECL);

    // TODO use Jena's SPARQL manipulation classes instead of strings
    // http://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

    if (StringUtils.isNotBlank(bindings)) {
      sparql.append(String.format("SELECT (count(?s) AS ?total) WHERE { ?s ?rel ?class . %1$s }", bindings));
    } else {
      sparql.append("SELECT (count(?s) AS ?total) WHERE { ?s ?rel ?class }");
    }

    LOG.trace("SPARQL:\n {}", sparql);

    final Model model = Jenabean.instance().model();
    final QuerySolutionMap initialBinding = new QuerySolutionMap();
    initialBinding.add("rel", model.createProperty(KBUtils.RDF_TYPE_REL));
    initialBinding.add("class", model.createResource(classType));
    final Query query = QueryFactory.create(sparql.toString());

    final QueryExecution qexec = QueryExecutionFactory.create(query, model, initialBinding);
    try {
      model.enterCriticalSection(Lock.READ);
      final ResultSet results = qexec.execSelect();
      if (results.hasNext()) {
        final QuerySolution soln = results.nextSolution();
        final Literal literal = soln.getLiteral("total");
        count = literal.getInt();
      } else {
        count = 0;
      }
    } finally {
      model.leaveCriticalSection();
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
