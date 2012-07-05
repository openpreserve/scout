package eu.scape_project.watch.dao;

import java.util.List;

import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.utils.KBUtils;

/**
 * {@link Source} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class SourceDAO extends AbstractDO<Source> {

  /**
   * Get the complete Source RDF Id to use in SPARQL.
   * 
   * @param source
   *          The name of the source.
   * @return The complete Source RDF Id using namescape prefix
   */
  public static String getSourceRDFId(final Source source) {
    return "<" + KBUtils.WATCH_NS + Source.class.getSimpleName() + "/" + source.getName() + ">";
  }

  /**
   * No other instances other then in {@link DAO}.
   */
  protected SourceDAO() {

  }

  /**
   * Get {@link Source} by its name.
   * 
   * @param sourceName
   *          the unique source name
   * @return The {@link Source} or <code>null</code> if not found
   */
  public Source findById(final String sourceName) {
    return super.findById(sourceName, Source.class);
  }

  /**
   * Query for {@link Source}.
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
   * @return A list of {@link Source} filtered by the above constraints
   */
  public List<Source> query(final String bindings, final int start, final int max) {
    return super.query(Source.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link Source}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(Source.class, bindings);
  }

  /**
   * Save a source into the knowledge base.
   * 
   * @param source
   *          The source to save.
   * @return The persisted source object.
   */
  public Source save(final Source source) {
    return super.saveImpl(source);
  }
}
