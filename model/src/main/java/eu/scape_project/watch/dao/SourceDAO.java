package eu.scape_project.watch.dao;

import java.util.List;

import eu.scape_project.watch.domain.PropertyValue;
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
    return KBUtils.getRdfId(Source.class, source.getId());
  }

  /**
   * No other instances other then in {@link DAO}.
   */
  protected SourceDAO() {

  }

  /**
   * Get {@link Source} by its name.
   * 
   * @param name
   *          the id of the source
   * @return The {@link Source} or <code>null</code> if not found
   */
  public Source findByName(final String name) {
    return super.findById(KBUtils.hashId(name), Source.class);
  }

  /**
   * Get {@link Source} by its id.
   * 
   * @param id
   *          the base 64 encoded id of the source
   * @return tThe {@link Source} or <code>null</code> if not found
   */
  public Source findById(final String id) {
    return super.findById(id, Source.class);
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

  private String getBindingsByPropertyValue(final PropertyValue propertyValue) {
    return "?measurement watch:propertyValue " + PropertyValueDAO.getPropertyValueRDFId(propertyValue)
      + ". ?measurement watch:adaptor ?adaptor . ?adaptor watch:source ?s";
  }

  public int countByPropertyValue(final PropertyValue propertyValue) {
    return count(getBindingsByPropertyValue(propertyValue));
  }
}
