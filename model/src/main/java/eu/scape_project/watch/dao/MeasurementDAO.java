package eu.scape_project.watch.dao;

import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;

/**
 * {@link Measurement} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class MeasurementDAO extends AbstractDO<Measurement> {
  /**
   * No other instances other then in {@link DAO}.
   */
  protected MeasurementDAO() {

  }

  /**
   * Get a measurement by its Id.
   * 
   * @param propertyName
   *          The name of the related property.
   * @param timestamp
   *          The measurement time stamp.
   * @return The unique measurement, or null if not found.
   */
  public Measurement findById(final String propertyName, final Date timestamp) {
    return super.findById(Measurement.createId(propertyName, timestamp), Measurement.class);
  }

  public Measurement findById(final String id) {
    return super.findById(id, Measurement.class);
  }

  /**
   * Query for {@link Measurement}.
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
   * @return A list of {@link Measurement} filtered by the above constraints
   */
  public List<Measurement> query(final String bindings, final int start, final int max) {
    return super.query(Measurement.class, bindings, start, max);
  }

  /**
   * Query for {@link Measurement}.
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
   * @param orderBy
   *          Set order of results using SPARQL syntax.
   * @return A list of {@link Measurement} filtered by the above constraints
   */
  public List<Measurement> query(final String bindings, final int start, final int max, final String orderBy) {
    return super.query(Measurement.class, bindings, new QuerySolutionMap(), start, max, orderBy);
  }

  /**
   * Count the results of a query for {@link Measurement}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(Measurement.class, bindings);
  }

  /**
   * Get the query string for getting all measurements related to a property
   * value.
   * 
   * @param value
   *          The property value to which all measurements are related.
   * @param showSignificantOnly
   * @return The query string.
   */
  private static String getListByPropertyValueQueryString(final PropertyValue value, final boolean showSignificantOnly) {
    String bindings;
    if (showSignificantOnly) {
      bindings = "?s watch:propertyValue " + PropertyValueDAO.getPropertyValueRDFId(value)
        + " . ?s watch:significant true";
    } else {
      bindings = "?s watch:propertyValue " + PropertyValueDAO.getPropertyValueRDFId(value);
    }

    return bindings;
  }

  /**
   * Get all measurements related to a property value.
   * 
   * @param value
   *          The property value from which we want all related measurements.
   * @param showSignificantOnly
   *          If <code>true</code> only show significant measurements, otherwise
   *          show all.
   * @param start
   *          The start index from which the return list should start.
   * @param max
   *          The maximum number of items to return.
   * @return A list of the measurements of this property, filtered by above
   *         constraints.
   */
  public List<Measurement> listByPropertyValue(final PropertyValue value, final boolean showSignificantOnly,
    final int start, final int max) {
    return query(getListByPropertyValueQueryString(value, showSignificantOnly), start, max);
  }

  /**
   * Get all measurements related to a property value.
   * 
   * @param value
   *          The property value from which we want all related measurements.
   * @param start
   *          The start index from which the return list should start.
   * @param max
   *          The maximum number of items to return.
   * @return A list of the measurements of this property, filtered by above
   *         constraints.
   */
  public List<Measurement> listByPropertyValue(final PropertyValue value, final int start, final int max) {
    return listByPropertyValue(value, false, start, max);
  }

  /**
   * Count the number of measurements related to a property value.
   * 
   * @param value
   *          The related property value.
   * @param showSignificantOnly
   *          If <code>true</code> only show significant measurements, otherwise
   *          show all.
   * @return The number of measurements that refer to the given property value.
   */
  public int countByPropertyValue(final PropertyValue value, final boolean showSignificantOnly) {
    return count(getListByPropertyValueQueryString(value, showSignificantOnly));
  }

  /**
   * Count the number of measurements related to a property value.
   * 
   * @param value
   *          The related property value.
   * @return The number of measurements that refer to the given property value.
   */
  public int countByPropertyValue(final PropertyValue value) {
    return countByPropertyValue(value, false);
  }

  /**
   * Get the query string for getting all measurements related to a source
   * adaptor.
   * 
   * @param adaptor
   *          The source adaptor to which all measurements are related.
   * @return The query string.
   */
  private static String getListByAdaptorQueryString(final SourceAdaptor adaptor) {
    return "?s watch:adaptor " + SourceAdaptorDAO.getSourceAdaptorRDFId(adaptor);
  }

  /**
   * Get all measurements related to a source adaptor.
   * 
   * @param adaptor
   *          The source adaptor from which we want all related measurements.
   * @param start
   *          The start index from which the return list should start.
   * @param max
   *          The maximum number of items to return.
   * @return A list of the measurements taken by this source adaptor, filtered
   *         by above constraints.
   */
  public List<Measurement> listByAdaptor(final SourceAdaptor adaptor, final int start, final int max) {
    return query(getListByAdaptorQueryString(adaptor), start, max);
  }

  /**
   * Count the number of measurements related to a source adaptor.
   * 
   * @param adaptor
   *          The related source adaptor.
   * @return The number of measurements taken by a source adaptor.
   */
  public int countByAdaptor(final SourceAdaptor adaptor) {
    return count(getListByAdaptorQueryString(adaptor));
  }

  /**
   * Get the query string for getting all measurements related to a source.
   * 
   * @param source
   *          The external source from which all measurements were taken.
   * @return The query string.
   */
  private static String getListBySourceQueryString(final Source source) {
    return "?s watch:adaptor ?adaptor . ?adaptor watch:source " + SourceDAO.getSourceRDFId(source);
  }

  /**
   * Get all measurements related to a source.
   * 
   * @param source
   *          The source from which we want all related measurements.
   * @param start
   *          The start index from which the return list should start.
   * @param max
   *          The maximum number of items to return.
   * @return A list of the measurements taken on this source, filtered by above
   *         constraints.
   */
  public List<Measurement> listBySource(final Source source, final int start, final int max) {
    return query(getListBySourceQueryString(source), start, max);
  }

  /**
   * Count the number of measurements related to a source.
   * 
   * @param source
   *          The related source.
   * @return The number of measurements taken in a source.
   */
  public int countBySource(final Source source) {
    return count(getListBySourceQueryString(source));
  }

  /**
   * Get the query string for getting all measurements related to a property.
   * 
   * @param property
   *          The property which values are all related to the measurements.
   * @return The query string.
   */
  private static String getListByPropertyQueryString(final Property property) {
    return "?s watch:propertyValue ?value . ?value watch:property " + PropertyDAO.getPropertyRDFId(property);
  }

  /**
   * Get all measurements related to a property.
   * 
   * @param property
   *          The property from which we want all related measurements.
   * @param start
   *          The start index from which the return list should start.
   * @param max
   *          The maximum number of items to return.
   * @return A list of the measurements of this property, filtered by above
   *         constraints.
   */
  public List<Measurement> listByProperty(final Property property, final int start, final int max) {
    return query(getListByPropertyQueryString(property), start, max);
  }

  /**
   * Count the number of measurements related to a property.
   * 
   * @param property
   *          The related property.
   * @return The number of measurements that refer to the given property.
   */
  public int countByProperty(final Property property) {
    return count(getListByPropertyQueryString(property));
  }

  /**
   * Get the query string for getting all measurements related to a property.
   * 
   * @param showSignificantOnly
   * 
   * @param property
   *          The property which values are all related to the measurements.
   * @return The query string.
   */
  private static String getBindingsByEntityAndProperty(final String entityId, final String propertyId,
    final boolean showSignificantOnly) {
    String bindings = "?s watch:propertyValue ?value . ?value watch:property "
      + PropertyDAO.getPropertyRDFId(propertyId) + " . ?value watch:entity " + EntityDAO.getEntityRDFId(entityId);
    if (showSignificantOnly) {
      bindings += ". ?s watch:significant true";
    }
    return bindings;
  }

  public List<Measurement> listByEntityAndProperty(final String entityId, final String propertyId,
    final boolean showSignificantOnly, final int start, final int max) {
    return query(getBindingsByEntityAndProperty(entityId, propertyId, showSignificantOnly), start, max);
  }

  public int countByEntityAndProperty(final String entityId, final String propertyId, final boolean showSignificantOnly) {
    return count(getBindingsByEntityAndProperty(entityId, propertyId, showSignificantOnly));
  }

  public Measurement findLastMeasurement(final PropertyValue pv) {
    Measurement ret = null;

    final String sparql = "?s watch:timestamp ?timestamp . ?s watch:propertyValue ?value . ?value watch:property "
      + PropertyDAO.getPropertyRDFId(pv.getProperty()) + " . ?value watch:entity "
      + EntityDAO.getEntityRDFId(pv.getEntity());

    final List<Measurement> list = query(sparql, 0, 1, "DESC(?timestamp)");
    if (list.size() > 0) {
      ret = list.get(0);
    }

    return ret;
  }
}
