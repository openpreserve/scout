package eu.scape_project.watch.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.utils.KBUtils;

/**
 * {@link SourceAdaptor} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class SourceAdaptorDAO extends AbstractDO<SourceAdaptor> {

  /**
   * Get the complete Source Adaptor RDF Id to use in SPARQL.
   * 
   * @param adaptor
   *          The name of the source adaptor.
   * @return The complete Source Adaptor RDF Id using namescape prefix
   */
  public static String getSourceAdaptorRDFId(final SourceAdaptor adaptor) {
    return KBUtils.getRdfId(SourceAdaptor.class, adaptor.getId());
  }

  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * No other instances other then in {@link DAO}.
   */
  protected SourceAdaptorDAO() {

  }

  /**
   * Get {@link SourceAdaptor} by its name and version.
   * 
   * @param name
   *          the source adaptor implementation name
   * @param version
   *          the source adaptor implementation version
   * @param instance
   *          the source adaptor instance identifier
   * @return The {@link SourceAdaptor} or <code>null</code> if not found
   */
  public SourceAdaptor findById(final String name, final String version, final String instance) {
    return super.findById(SourceAdaptor.createId(name, version, instance), SourceAdaptor.class);
  }

  /**
   * Query for {@link SourceAdaptor}.
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
   * @return A list of {@link SourceAdaptor} filtered by the above constraints
   */
  public List<SourceAdaptor> query(final String bindings, final int start, final int max) {
    return super.query(SourceAdaptor.class, bindings, start, max);
  }

  /**
   * Count the results of a query for {@link SourceAdaptor}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(SourceAdaptor.class, bindings);
  }

  /**
   * Get query string that finds all source adaptors that are related to a given
   * source.
   * 
   * @param source
   *          The related external source description.
   * @return A query string that binds the source.
   */
  private static String getListBySourceQueryString(final Source source) {
    return "?s watch:source " + SourceDAO.getSourceRDFId(source);
  }

  /**
   * Get all adaptors of a defined source.
   * 
   * @param source
   *          The related external source description.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link SourceAdaptor} filtered by the above constraints
   */
  public List<SourceAdaptor> listBySource(final Source source, final int start, final int max) {
    return query(getListBySourceQueryString(source), start, max);
  }

  /**
   * Count the number of adaptors of a given souce.
   * 
   * @param source
   *          The related external source description.
   * @return The number of adaptors related to the given source.
   */
  public int countBySource(final Source source) {
    return super.count(SourceAdaptor.class, getListBySourceQueryString(source));
  }

  /**
   * Get query string that finds all versions of a source adaptor, searching by
   * name.
   * 
   * @param sourceAdaptorName
   *          The name of the source adaptor, to get all the versions.
   * @return A query string that binds the source adaptor name.
   */
  private static String getListByNameQueryString(final String sourceAdaptorName) {
    return String.format("?s watch:name \"%1$s\"^^xsd:string", sourceAdaptorName);
  }

  /**
   * Get all versions of an adaptor, searching by its name.
   * 
   * @param name
   *          The name of the source adaptor for which to get all versions.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link SourceAdaptor} filtered by the above constraints
   */
  public List<SourceAdaptor> listByName(final String name, final int start, final int max) {
    return query(getListByNameQueryString(name), start, max);
  }

  /**
   * Count all versions of an adaptor, searching by its name.
   * 
   * @param name
   *          The name of the source adaptor, to get all the versions.
   * @return The number of an adaptor, defined by its name.
   */
  public int countByName(final String name) {
    return super.count(SourceAdaptor.class, getListByNameQueryString(name));
  }

  /**
   * Save a source adaptor into the knowledge base.
   * 
   * @param sourceAdaptor
   *          The source adaptor to save.
   * @return The persisted source adaptor object.
   */
  public SourceAdaptor save(final SourceAdaptor sourceAdaptor) {
    return super.saveImpl(sourceAdaptor);
  }

  /**
   * Delete Property Value and related Measurement.
   * 
   * @param pv
   *          The property value to delete.
   * @return the deleted property value.
   */
  @Override
  public SourceAdaptor delete(final SourceAdaptor adaptor) {

    // Delete all related measurements
    final int measurementCount = DAO.MEASUREMENT.countByAdaptor(adaptor);
    final Set<PropertyValue> relatedProperties = new HashSet<PropertyValue>();
    int i = 0;
    while (i < measurementCount) {
      final List<Measurement> measurements = DAO.MEASUREMENT.listByAdaptor(adaptor, i, 100);
      for (Measurement measurement : measurements) {
        final PropertyValue propertyValue = measurement.getPropertyValue();
        if (propertyValue != null) {
          relatedProperties.add(propertyValue);
        }
        DAO.MEASUREMENT.delete(measurement);
      }
      i += measurements.size();
    }

    // clean up property values with no related measurements
    for (PropertyValue pv : relatedProperties) {
      final int pvMeasurementCount = DAO.MEASUREMENT.countByPropertyValue(pv);
      if (pvMeasurementCount == 0) {
        DAO.PROPERTY_VALUE.delete(pv);
      }
    }

    // delete all related source adaptor events
    final int eventCount = DAO.SOURCE_ADAPTOR_EVENT.countByAdaptor(adaptor);
    int j = 0;
    while (j < eventCount) {
      final List<SourceAdaptorEvent> events = DAO.SOURCE_ADAPTOR_EVENT.listByAdaptor(adaptor, j, 100);
      for (SourceAdaptorEvent event : events) {
        DAO.SOURCE_ADAPTOR_EVENT.delete(event);
      }
      j += events.size();
    }

    return super.delete(adaptor);
  }

  /**
   * Get all Source Adaptors.
   * 
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of source adaptors, filtered by the constraints above.
   */
  public List<SourceAdaptor> queryAll(final int start, final int max) {
    return query("", start, max);
  }

  /**
   * Get the total count of Source Adaptors.
   * 
   * @return The total number of source adaptors.
   */
  public int countAll() {
    return count("");
  }

  /**
   * Get query string that finds all active/inactive source adaptor.
   * 
   * @param active
   *          <code>true</code> to find all active source adaptors,
   *          <code>false</code> to find all inactive.
   * @return A query string that binds the source adaptor active flag.
   */
  private static String getListOfActiveQueryString(final boolean active) {
    return String.format("?s watch:active \"%1$s\"^^xsd:boolean", active);
  }

  /**
   * Get all active/inactive Source Adaptors.
   * 
   * @param active
   *          <code>true</code> to find all active source adaptors,
   *          <code>false</code> to find all inactive.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of source adaptors, filtered by the constraints above.
   */
  public List<SourceAdaptor> queryActive(final boolean active, final int start, final int max) {
    return query(getListOfActiveQueryString(active), start, max);
  }

  /**
   * Get the total count of Source Adaptors.
   * 
   * @param active
   *          <code>true</code> to count all active source adaptors,
   *          <code>false</code> to count all inactive.
   * @return The total number of source adaptors.
   */
  public int countActive(final boolean active) {
    return count(getListOfActiveQueryString(active));
  }

  // TODO list and count of source adaptor instances (same name and version)

}
