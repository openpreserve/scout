package eu.scape_project.watch.dao;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.utils.JavaUtils;
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

  private final Logger logger = LoggerFactory.getLogger(getClass());

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
   * @param entityId
   *          The entity related to the value.
   * @param propertyId
   *          The property related to the value.
   * @param asOfDate
   *          The date before which to search of the defined property value.
   * @return The last set value for that property up to the given date, or null
   *         if no value was defined previous to the given date.
   */
  public PropertyValue find(final String entityId, final String propertyId, final Date asOfDate) {

    // find all PropertyValue related to the entity and property, ordered by
    // measured date, and return the most recent one.

    final StringBuilder query = new StringBuilder();

    query.append("?s watch:entity " + EntityDAO.getEntityRDFId(entityId));
    query.append(" . ");
    query.append("?s watch:property " + PropertyDAO.getPropertyRDFId(propertyId));
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

    final List<PropertyValue> pvs = query(query.toString(), 0, 1, "DESC(?timestamp)");

    PropertyValue ret = null;

    if (!pvs.isEmpty()) {
      ret = pvs.get(0);
    }

    return ret;
  }

  /**
   * Find a property value, as last set on current date.
   * 
   * @param entityId
   *          The entity related to the property value.
   * @param propertyId
   *          The property related to the value..
   * @return The last set value for that property up to the current date, or
   *         null if no value was defined up till now.
   */
  public PropertyValue find(final String entityId, final String propertyId) {
    return find(entityId, propertyId, null);
  }

  public PropertyValue findById(final String valueId) {
    return findById(valueId, PropertyValue.class);
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
    return super.query(PropertyValue.class, bindings, new QuerySolutionMap(), start, max, orderBy);
  }

  public List<PropertyValue> query(final String bindings, final QuerySolutionMap variableBindings, final int start,
    final int max) {
    return super.query(PropertyValue.class, bindings, variableBindings, start, max);
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

  private String getBindingsWithEntityAndProperty(final String entityId, final String propertyId) {
    return String.format("?s %1$s %2$s . ?s %3$s %4$s", ENTITY_REL, EntityDAO.getEntityRDFId(entityId), PROPERTY_REL,
      PropertyDAO.getPropertyRDFId(propertyId));
  }

  /**
   * List all property values of a specific {@link Entity} and {@link Property}.
   * 
   * @param entityId
   *          The id of the {@link Entity}.
   * @param propertyId
   *          The id of the {@link Property}.
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of {@link PropertyValue} filtered by the above constraints
   */
  public Collection<PropertyValue> listWithEntityAndProperty(final String entityId, final String propertyId,
    final int start, final int max) {
    return this.query(getBindingsWithEntityAndProperty(entityId, propertyId), start, max);
  }

  public int countWithEntityAndProperty(final String entityId, final String propertyId) {
    return this.count(getBindingsWithEntityAndProperty(entityId, propertyId));
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
    PropertyValue ret;

    // check if exists a property value with that value
    final Entity entity = pv.getEntity();
    final Property property = pv.getProperty();

    final String valuePredicate = getValuePredicate(property.getDatatype());
    final Literal valueLiteral = createValueLiteral(pv.getValue(), property.getDatatype());
    final boolean canBindValue = valuePredicate != null && valueLiteral != null;

    final QuerySolutionMap variableBinding = new QuerySolutionMap();
    if (canBindValue) {
      variableBinding.add("value", valueLiteral);
    }

    String valueBinding = "";
    if (canBindValue) {
      valueBinding = " . ?s " + valuePredicate + " ?value";
    }

    final List<PropertyValue> existingPVs = query("?s watch:entity " + EntityDAO.getEntityRDFId(entity)
      + " . ?s watch:property " + PropertyDAO.getPropertyRDFId(property) + valueBinding, variableBinding, 0, 1);

    final Measurement previousMeasurement = DAO.MEASUREMENT.findLastMeasurement(pv);

    boolean createNewVersion;

    if (canBindValue) {
      createNewVersion = existingPVs.isEmpty();
    } else {
      if (property.getDatatype().equals(DataType.STRING_DICTIONARY)) {
        List<DictionaryItem> currentValue = (List) pv.getValue();
        if (!existingPVs.isEmpty()) {
          List<DictionaryItem> previousValue = (List) existingPVs.get(0).getValue();
          createNewVersion = !currentValue.equals(previousValue);
        } else {
          createNewVersion = true;
        }

      } else if (property.getDatatype().equals(DataType.STRING_LIST)) {
        List<String> currentValue = (List) pv.getValue();
        if (!existingPVs.isEmpty()) {
          List<String> previousValue = (List) existingPVs.get(0).getValue();
          createNewVersion = !currentValue.equals(previousValue);
        } else {
          createNewVersion = true;
        }
      } else {
        createNewVersion = true;
      }
    }

    if (createNewVersion) {
      logger.info("Creating new PV");

      // create new property value with a new version.
      final int version = getNextVersionNumber(entity, property);
      pv.setVersion(version);

      // Set previous measurement limit
      if (previousMeasurement != null) {
        previousMeasurement.setLimit(true);
        previousMeasurement.setLast(false);
        previousMeasurement.save();
      }

      // Save current measurement
      final Measurement measurement = new Measurement(pv, asOfDate, adaptor);
      measurement.setLimit(true);
      measurement.setLast(true);
      measurement.save();

      ret = super.saveImpl(pv);

    } else {
      logger.info("Adding measurement to existing PV");

      // tag existing property value with a new measurement.
      final PropertyValue existingPV = existingPVs.get(0);

      // Set previous measurement NOT the last measurement
      if (previousMeasurement != null) {
        previousMeasurement.setLast(false);
        previousMeasurement.save();
      }

      // Save new measurement and set it significant
      final Measurement measurement = new Measurement(existingPV, asOfDate, adaptor);
      measurement.setLast(true);
      measurement.save();

      ret = existingPV;
    }

    return ret;
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
  private Literal createValueLiteral(final Object value, final DataType datatype) {
    // TODO support StringList and StringDictionary

    Literal literal;
    final Model model = Jenabean.instance().model();

    switch (datatype) {
      case STRING:
        final String stringValue = (String) value;
        literal = model.createTypedLiteral(stringValue);
        break;
      case INTEGER:
        final int intValue = (Integer) value;
        literal = model.createTypedLiteral(intValue);
        break;
      case LONG:
        final long longValue = (Long) value;
        literal = model.createTypedLiteral(longValue);
        break;
      case FLOAT:
        final float floatValue = (Float) value;
        literal = model.createTypedLiteral(floatValue);
        break;
      case DOUBLE:
        final double doubleValue = (Double) value;
        literal = model.createTypedLiteral(doubleValue);
        break;
      case DATE:
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) value);

        literal = model.createTypedLiteral(calendar);
        break;
      case URI:
        URI uriValue = (URI) value;
        literal = model.createTypedLiteral(uriValue);
        break;
      default:
        literal = model.createTypedLiteral(value);
    }
    return literal;
  }

  private String getValuePredicate(final DataType datatype) {
    String predicate;
    switch (datatype) {
      case STRING:
        predicate = "watch:stringValue";
        break;
      case INTEGER:
        predicate = "watch:integerValue";
        break;
      case LONG:
        predicate = "watch:longValue";
        break;
      case FLOAT:
        predicate = "watch:floatValue";
        break;
      case DOUBLE:
        predicate = "watch:doubleValue";
        break;
      case DATE:
        predicate = "watch:dateValue";
        break;
      case URI:
        predicate = "watch:uriValue";
        break;
      default:
        return null;
    }
    return predicate;
  }

  public static void main(String[] args) throws IOException {
    File dataTempir = JavaUtils.createTempDirectory();
    KBUtils.dbConnect(dataTempir.getPath(), false);
    String test = "ME (Millenium Edition)\\\n.9)";
    PropertyValueDAO dao = new PropertyValueDAO();
    System.out.println("binding: " + dao.createValueLiteral(test, DataType.STRING));
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempir);
  }

  // TODO list all property values by adaptor
  // TODO list all property values by source
}
