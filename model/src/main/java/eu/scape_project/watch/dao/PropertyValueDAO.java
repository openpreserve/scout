package eu.scape_project.watch.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.utils.KBUtils;

/**
 * {@link PropertyValue} Data Access Object.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class PropertyValueDAO extends AbstractDO<PropertyValue> {

  /**
   * The name of the relationship to {@link Entity} in {@link PropertyValue}.
   */
  private static final String ENTITY_REL = KBUtils.WATCH_PREFIX + "entity";

  /**
   * The name of the relationship to {@link Property} in {@link PropertyValue} .
   */
  private static final String PROPERTY_REL = KBUtils.WATCH_PREFIX + "property";

  /**
   * Get the complete Property Value RDF Id to use in SPARQL.
   * 
   * @param propertyValue
   *          The property value from which go get the RDF id.
   * 
   * @return The complete Property Value RDF Id using namescape prefix
   */
  public static String getPropertyValueRDFId(final PropertyValue propertyValue) {
    return KBUtils.getRdfId(PropertyValue.class, propertyValue.getId());
  }

  /**
   * No other instances other then in {@link DAO}.
   */
  protected PropertyValueDAO() {

  }

  /**
   * Find {@link PropertyValue} by the related {@link Entity} and
   * {@link Property}.
   * 
   * @param typeName
   *          The name of the related {@link EntityType}
   * @param entityName
   *          The name of the related {@link Entity}
   * @param propertyName
   *          The name of the related {@link Property}
   * @param version
   *          The version number of the required property value.
   * @return The {@link PropertyValue} or <code>null</code> if not found
   */
  public PropertyValue find(final String typeName, final String entityName, final String propertyName, final int version) {
    final String id = PropertyValue.createId(typeName, entityName, propertyName, version);
    return super.findById(id, PropertyValue.class);
  }

  /**
   * Find a property value, as last set on a determined date.
   * 
   * @param entityName
   *          The entity related to the property.
   * @param entityType
   *          The entity type related to the property.
   * @param propertyName
   *          The property name.
   * @param asOfDate
   *          The date before which to search of the defined property value.
   * @return The last set value for that property up to the given date, or null
   *         if no value was defined previous to the given date.
   */
  public PropertyValue find(final String entityName, final String entityType, final String propertyName,
    final Date asOfDate) {

    // find all PropertyValue related to the entity and property, ordered by
    // measured date, and return the most recent one.

    final StringBuilder query = new StringBuilder();

    query.append("?s watch:entity " + EntityDAO.getEntityRDFId(entityType, entityName));
    query.append(" . ");
    query.append("?s watch:property " + PropertyDAO.getPropertyRDFId(entityType, propertyName));
    query.append(" . ");
    query.append("?measurement watch:propertyValue ?s");
    if (asOfDate != null) {
      query.append(" . ");
      query.append("?measurement watch:timestamp ?timestamp");
      query.append(" . ");
      final Calendar asOfCalendar = Calendar.getInstance();
      asOfCalendar.setTime(asOfDate);
      query.append(String.format("FILTER(?timestamp <= \"%1$s\"^^xsd:dateTime)", new XSDDateTime(asOfCalendar)));
    }

    final List<PropertyValue> pvs = query(query.toString(), 0, 1, "DESC(?measurement)");

    PropertyValue ret = null;

    if (!pvs.isEmpty()) {
      ret = pvs.get(0);
    }

    return ret;
  }

  /**
   * Find a property value, as last set on current date.
   * 
   * @param entityName
   *          The entity related to the property.
   * @param entityType
   *          The entity type related to the property.
   * @param propertyName
   *          The property name.
   * @return The last set value for that property up to the current date, or
   *         null if no value was defined up till now.
   */
  public PropertyValue find(final String entityName, final String entityType, final String propertyName) {
    return find(entityName, entityType, propertyName, null);
  }

  /**
   * Query for {@link PropertyValue}.
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
   * @return A list of {@link PropertyValue} filtered by the above constraints
   */
  public List<PropertyValue> query(final String bindings, final int start, final int max) {
    return super.query(PropertyValue.class, bindings, start, max);
  }

  /**
   * Query for {@link PropertyValue}.
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
   *          Inject a order by clause, arguments of the ORDER BY clause, e.g.
   *          DESC(?s). If null then no ORDER BY clause is added.
   * @return A list of {@link PropertyValue} filtered by the above constraints
   */
  public List<PropertyValue> query(final String bindings, final int start, final int max, final String orderBy) {
    return super.query(PropertyValue.class, bindings, start, max, orderBy);
  }

  /**
   * Count the results of a query for {@link PropertyValue}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(PropertyValue.class, bindings);
  }

  /**
   * List all property values of a specific {@link Entity}.
   * 
   * @param entityName
   *          The name of the {@link Entity}
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of {@link PropertyValue} filtered by the above constraints
   */
  public List<PropertyValue> listWithEntity(final String typeName, final String entityName, final int start,
    final int max) {
    final String bindings = String.format("?s %1$s %2$s", ENTITY_REL, EntityDAO.getEntityRDFId(typeName, entityName));
    return this.query(bindings, start, max);
  }

  public List<PropertyValue> listWithEntity(final Entity entity, final int start, final int max) {
    final String bindings = String.format("?s %1$s %2$s", ENTITY_REL, EntityDAO.getEntityRDFId(entity));
    return this.query(bindings, start, max);
  }

  private String getBindingsWithEntityAndProperty(final String entityType, final String entityName,
    final String propertyName) {
    return String.format("?s %1$s %2$s . ?s %3$s %4$s", ENTITY_REL, EntityDAO.getEntityRDFId(entityType, entityName),
      PROPERTY_REL, PropertyDAO.getPropertyRDFId(entityType, propertyName));
  }

  /**
   * List all property values of a specific {@link Entity} and {@link Property}.
   * 
   * @param entityName
   *          The name of the {@link Entity}.
   * @param entityType
   *          The name of the {@link EntityType} to which the {@link Property}
   *          belongs to
   * @param propertyName
   *          The name of the {@link Property}.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of {@link PropertyValue} filtered by the above constraints
   */
  public Collection<PropertyValue> listWithEntityAndProperty(final String entityType, final String entityName,
    final String propertyName, final int start, final int max) {
    return this.query(getBindingsWithEntityAndProperty(entityType, entityName, propertyName), start, max);
  }

  public int countWithEntityAndProperty(final String entityType, final String entityName, final String propertyName) {
    return this.count(getBindingsWithEntityAndProperty(entityType, entityName, propertyName));
  }

  /**
   * List all property values of a {@link Property} independently of the
   * {@link Entity}.
   * 
   * @param entityType
   *          The name of the {@link EntityType} to which the {@link Property}
   *          belongs to
   * @param propertyName
   *          The name of the {@link Property}.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of {@link PropertyValue} filtered by the above constraints
   */
  public Collection<PropertyValue> listWithProperty(final String entityType, final String propertyName,
    final int start, final int max) {
    final String bindings = String.format("?s %1$s %2$s", PROPERTY_REL,
      PropertyDAO.getPropertyRDFId(entityType, propertyName));
    return this.query(bindings, start, max);
  }

  /**
   * Count properties values that have no associated measurements.
   * 
   * @return The number of prorperty values with no associated measurements
   */
  public int countMeasurementOrphan() {
    return count("{SELECT (count(?m) as ?cm) {?m rdf:type watch:" + Measurement.class.getSimpleName()
      + " . ?m watch:propertyValue ?s}} FILTER(?cm=0)");
  }

  /**
   * Save the value of a property, creating a new value if it doesn't exist and
   * linking to a measurement with current date and time. If the value already
   * exists, than only a new measurement will be created. The measurement must
   * define the adaptor that took it.
   * 
   * @param adaptor
   *          The adaptor that took the measurement of this property.
   * @param pv
   *          The value of the property to save
   * 
   * 
   * @return The final {@link PropertyValue}
   */
  public PropertyValue save(final SourceAdaptor adaptor, final PropertyValue pv) {
    return save(adaptor, new Date(), pv);
  }

  /**
   * Save a list of property values, using
   * {@link #save(SourceAdaptor, PropertyValue)}.
   * 
   * @param adaptor
   *          The adaptor that took the measurement of this property.
   * @param pvs
   *          The values of the properties to save
   * @return A list with the saved {@link PropertyValue}
   */
  public List<PropertyValue> save(final SourceAdaptor adaptor, final PropertyValue... pvs) {
    final List<PropertyValue> ret = new ArrayList<PropertyValue>();

    for (PropertyValue pv : pvs) {
      ret.add(save(adaptor, pv));
    }

    return ret;
  }

  /**
   * Save the value of a property, creating a new value if it doesn't exist and
   * linking to a measurement with given date and time. If the value already
   * exists, than only a new measurement will be created.
   * 
   * @param pv
   *          The value of the property to save
   * @param asOfDate
   *          The data and time to set on the measurement
   * @param adaptor
   *          The adaptor that took the measurement of the property.
   * @return The final {@link PropertyValue}
   */
  public synchronized PropertyValue save(final SourceAdaptor adaptor, final Date asOfDate, final PropertyValue pv) {
    // check if exists a property value with that value
    final Entity entity = pv.getEntity();
    final Property property = pv.getProperty();
    final String valueBinding = createValueBinding(pv.getValue(), property.getDatatype());

    final List<PropertyValue> existingPVs = query("?s watch:entity " + EntityDAO.getEntityRDFId(entity)
      + " . ?s watch:property " + PropertyDAO.getPropertyRDFId(property) + " . " + valueBinding, 0, 1);

    if (existingPVs.isEmpty()) {
      // create new property value with a new version.
      final int version = getNextVersionNumber(entity, property);
      pv.setVersion(version);
      final Measurement measurement = new Measurement(pv, asOfDate, adaptor);
      measurement.save();
    } else {
      // tag existing property value with a new measurement.
      final PropertyValue existingPV = existingPVs.get(0);
      final Measurement measurement = new Measurement(existingPV, asOfDate, adaptor);
      measurement.save();
    }

    return super.saveImpl(pv);
  }

  /**
   * Delete Property Value and related Measurement.
   * 
   * @param pv
   *          The property value to delete.
   * @return the deleted property value.
   */
  @Override
  public PropertyValue delete(final PropertyValue pv) {

    final int count = DAO.MEASUREMENT.countByPropertyValue(pv);

    int i = 0;
    while (i < count) {
      final List<Measurement> measurements = DAO.MEASUREMENT.listByPropertyValue(pv, i, 100);
      for (Measurement measurement : measurements) {
        DAO.MEASUREMENT.delete(measurement);
      }
      i += measurements.size();
    }

    return super.delete(pv);
  }

  /**
   * Get a version number for a PropertyValue.
   * 
   * @param entity
   *          The property value related entity.
   * @param property
   *          The property value related property.
   * @return The next version number, which will be 0 if none exist.
   */
  public int getNextVersionNumber(final Entity entity, final Property property) {
    return count("?s watch:entity " + EntityDAO.getEntityRDFId(entity) + " . ?s watch:property "
      + PropertyDAO.getPropertyRDFId(property));
  }

  /**
   * Create the query binding for a value of a determined data type.
   * 
   * @param value
   *          the value content.
   * @param datatype
   *          the type of data contained.
   * @return The query binding, using ?s as subject, which should be the id of
   *         the PropertValue.
   */
  private String createValueBinding(final Object value, final DataType datatype) {
    final StringBuilder ret = new StringBuilder();

    String predicate;
    String rdfDataType;

    // TODO support StringList and StringDictionary

    switch (datatype) {
      case STRING:
        predicate = "watch:stringValue";
        rdfDataType = "xsd:string";
        break;
      case INTEGER:
        predicate = "watch:integerValue";
        rdfDataType = "xsd:integer";
        break;
      case LONG:
        predicate = "watch:longValue";
        rdfDataType = "xsd:long";
        break;
      case FLOAT:
        predicate = "watch:floatValue";
        rdfDataType = "xsd:float";
        break;
      case DOUBLE:
        predicate = "watch:doubleValue";
        rdfDataType = "xsd:double";
        break;
      case DATE:
        predicate = "watch:dateValue";
        rdfDataType = "xsd:date";
        break;
      case URI:
        predicate = "watch:uriValue";
        rdfDataType = "xsd:uri";
        break;
      default:
        return "";
    }

    ret.append("?s ");
    ret.append(predicate);
    ret.append(" '");
    ret.append(value);
    ret.append("'^^");
    ret.append(rdfDataType);

    return ret.toString();
  }

  // TODO list all property values by adaptor
  // TODO list all property values by source
}
