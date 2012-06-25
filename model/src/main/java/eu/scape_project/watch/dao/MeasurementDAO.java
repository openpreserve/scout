package eu.scape_project.watch.dao;

import java.util.Date;
import java.util.List;

import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.KBUtils;

/**
 * {@link Measurement} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class MeasurementDAO extends AbstractDO<Measurement> {

  /**
   * Get the query string for getting all measurements related to a property
   * value.
   * 
   * @param value
   *          The property value to which all measurements are related.
   * @return The query string.
   */
  private static String getPropertyValueQueryString(final PropertyValue value) {
    return "?s watch:propertyValue " + KBUtils.WATCH_PROPERTY_VALUE_PREFIX + value.getId();
  }

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
  public List<Measurement> query(final PropertyValue value, final int start, final int max) {
    return query(getPropertyValueQueryString(value), start, max);
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
   * Count the number of measurements related to a property value.
   * 
   * @param value
   *          The related property value.
   * @return The number of measurements that refer to the given property value.
   */
  public int count(final PropertyValue value) {
    return super.count(Measurement.class, getPropertyValueQueryString(value));
  }

}
