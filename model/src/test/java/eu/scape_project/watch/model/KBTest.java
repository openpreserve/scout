package eu.scape_project.watch.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.Jenabean;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.dao.EntityTypeDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.dao.PropertyValueDAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.domain.SourceAdaptorEventType;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * 
 * Unit tests of the Knowledge Base persistence and access.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class KBTest {
  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(KBTest.class);

  // Removing '%' to reduce logs
  // private static final String STRANGE_CHARS = " âñüç!\"<>«»€#%";
  private static final String STRANGE_CHARS = " âñüç!\"<>«»€#";

  private static final String DATA_TYPE_NAME = "Entity type name" + STRANGE_CHARS;
  private static final String DATA_TYPE_DESC = "Entity type description" + STRANGE_CHARS;
  private static final String DATA_PROPERTY_NAME = "Property name" + STRANGE_CHARS;
  private static final String DATA_PROPERTY_DESC = "Property description" + STRANGE_CHARS;
  private static final String DATA_ENTITY_NAME = "Entity name" + STRANGE_CHARS;
  private static final String DATA_SOURCE_NAME = "Source name" + STRANGE_CHARS;
  private static final String DATA_SOURCE_DESCRIPTION = "Source description" + STRANGE_CHARS;
  private static final String DATA_PLUGIN_NAME = "Plugin adaptor" + STRANGE_CHARS;
  private static final String DATA_PLUGIN_VERSION = "0.0.1-SNAPSHOT";
  private static final String DATA_SOURCE_ADAPTOR_INSTANCE = "My default instance" + STRANGE_CHARS;

  private static final String DATA_TYPE_NAME_ALT = "Alternative entity type name" + STRANGE_CHARS;
  private static final String DATA_TYPE_DESC_ALT = "Alternative entity type description" + STRANGE_CHARS;

  /**
   * A temporary directory to hold the data.
   */
  private File dataTempir;

  /**
   * Initialize the data folder.
   * 
   * @throws IOException
   *           Error creating temporary data folder
   */
  @Before
  public void before() throws IOException {
    dataTempir = JavaUtils.createTempDirectory();
    KBUtils.dbConnect(dataTempir.getPath(), false);
  }

  /**
   * Cleanup the data folder.
   */
  @After
  public void after() {
    LOG.info("Deleting data folder at " + dataTempir);
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempir);
  }

  /**
   * Testing equals of EntityType.
   */
  @Test
  public void testEntityTypeEquals() {
    final String name = "name";
    final String description = "description";

    final EntityType type1 = new EntityType(name, description);
    final EntityType type2 = new EntityType();
    type2.setName(name);
    type2.setDescription(description);

    Assert.assertTrue(type1.equals(type2));
  }

  /**
   * Test Entity Type CRUD operations.
   */
  @Test
  public void testEntityTypeCRUD() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    DAO.save(type);

    // List
    final Collection<EntityType> types = Jenabean.instance().reader().load(EntityType.class);

    Assert.assertTrue(types.contains(type));

    // QUERY
    final List<EntityType> types2 = DAO.ENTITY_TYPE.query("", 0, 100);

    Assert.assertTrue(types2.contains(type));

    // FIND
    final EntityType type2 = DAO.ENTITY_TYPE.findById(type.getId());

    Assert.assertNotNull(type2);
    Assert.assertEquals(type, type2);

    // COUNT
    final int count = DAO.ENTITY_TYPE.count("");
    Assert.assertEquals(1, count);

    // DELETE
    DAO.delete(type);

    // LIST AGAIN
    final Collection<EntityType> types3 = Jenabean.instance().reader().load(EntityType.class);

    Assert.assertFalse(types3.contains(type));

    // QUERY AGAIN
    final List<EntityType> types4 = DAO.ENTITY_TYPE.query("", 0, 100);

    Assert.assertFalse(types4.contains(type));

    // FIND AGAIN
    final EntityType type3 = DAO.ENTITY_TYPE.findById(type.getName());
    Assert.assertNull(type3);

    // COUNT AGAIN
    final int count2 = DAO.ENTITY_TYPE.count("");
    Assert.assertEquals(0, count2);

  }

  /**
   * Test Entity Type CRUD listeners.
   */
  @Test
  public void testEntityTypeListeners() {

    @SuppressWarnings("unchecked")
    final DOListener<EntityType> mockDOListener = Mockito.mock(DOListener.class);

    DAO.addDOListener(EntityType.class, mockDOListener);

    final EntityType type1 = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);
    final EntityType type2 = new EntityType(DATA_TYPE_NAME_ALT, DATA_TYPE_DESC_ALT);

    DAO.save(type1, type2);
    Mockito.verify(mockDOListener).onUpdated(type1);
    Mockito.verify(mockDOListener).onUpdated(type2);

    DAO.delete(type1, type2);
    Mockito.verify(mockDOListener).onRemoved(type1);
    Mockito.verify(mockDOListener).onRemoved(type2);

    DAO.removeDOListener(EntityType.class, mockDOListener);

  }

  /**
   * Test Property equals and hashcode.
   */
  @Test
  public void testPropertyEquals() {

    final EntityType type1 = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);
    final EntityType type2 = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);

    final String name = DATA_PROPERTY_NAME;
    final String description = DATA_PROPERTY_DESC;

    final Property property1 = new Property(type1, name, description);
    final Property property2 = new Property();
    property2.setType(type2);
    property2.setName(name);
    property2.setDescription(description);

    Assert.assertTrue(property1.equals(property2));
  }

  /**
   * Test Property CRUD operations.
   */
  @Test
  public void testPropertyCRUD() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    DAO.save(type);
    DAO.save(property);

    // List
    final Collection<Property> properties = Jenabean.instance().reader().load(Property.class);
    Assert.assertTrue(properties.contains(property));

    // QUERY
    final List<Property> properties2 = DAO.PROPERTY.query("", 0, 100);

    Assert.assertTrue(properties2.contains(property));

    // FIND
    final Property property2 = DAO.PROPERTY.findByEntityTypeAndName(type.getName(), property.getName());

    Assert.assertNotNull(property2);
    Assert.assertEquals(property, property2);

    // COUNT
    final int count = DAO.PROPERTY.count("");
    Assert.assertEquals(1, count);

    // DELETE
    DAO.delete(type);
    DAO.delete(property);

    // LIST AGAIN
    final Collection<Property> properties3 = Jenabean.instance().reader().load(Property.class);

    Assert.assertFalse(properties3.contains(property));

    // QUERY AGAIN
    final List<Property> properties4 = DAO.PROPERTY.query("", 0, 100);

    Assert.assertFalse(properties4.contains(property));

    // FIND AGAIN
    final Property property3 = DAO.PROPERTY.findByEntityTypeAndName(type.getName(), property.getName());
    Assert.assertNull(property3);

    // COUNT AGAIN
    final int count2 = DAO.PROPERTY.count("");
    Assert.assertEquals(0, count2);

  }

  /**
   * Testing Property listing methods.
   */
  @Test
  public void testPropertyListings() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    DAO.save(type);
    DAO.save(property);

    final Collection<Property> properties1 = DAO.PROPERTY.listWithType(type.getId(), 0, 100);
    Assert.assertTrue(properties1.contains(property));

    // DELETE
    DAO.delete(type);
    DAO.delete(property);
  }

  /**
   * Testing Entity equals and hashcode.
   */
  @Test
  public void testEntityEquals() {

    final EntityType type1 = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);
    final EntityType type2 = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);

    final String name = DATA_ENTITY_NAME;

    final Entity entity1 = new Entity(type1, name);
    final Entity entity2 = new Entity();
    entity2.setType(type2);
    entity2.setName(name);

    Assert.assertTrue(entity1.equals(entity2));
  }

  /**
   * Test Entity CRUD operations.
   */
  @Test
  public void testEntityCRUD() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);

    DAO.save(type);
    DAO.save(entity);

    // List
    final Collection<Entity> entities = Jenabean.instance().reader().load(Entity.class);
    Assert.assertTrue(entities.contains(entity));

    // QUERY
    final List<Entity> entities2 = DAO.ENTITY.query("", 0, 100);

    Assert.assertTrue(entities2.contains(entity));

    // FIND
    final Entity entity2 = DAO.ENTITY.findById(type.getName(), entity.getName());

    Assert.assertNotNull(entity2);
    Assert.assertEquals(entity, entity2);

    // COUNT
    final int count = DAO.ENTITY.count("");
    Assert.assertEquals(1, count);

    // DELETE
    DAO.delete(type);
    DAO.delete(entity);

    // LIST AGAIN
    final Collection<Entity> entities3 = Jenabean.instance().reader().load(Entity.class);
    Assert.assertFalse(entities3.contains(entity));

    // QUERY AGAIN
    final List<Entity> entities4 = DAO.ENTITY.query("", 0, 100);
    Assert.assertFalse(entities4.contains(entity));

    // FIND AGAIN
    final Entity entity3 = DAO.ENTITY.findById(type.getName(), entity.getName());
    Assert.assertNull(entity3);

    // COUNT AGAIN
    final int count2 = DAO.ENTITY.count("");
    Assert.assertEquals(0, count2);
  }

  /**
   * Testing entity listing methods.
   */
  @Test
  public void testEntityListings() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);

    DAO.save(type);
    DAO.save(entity);

    final Collection<Entity> entities1 = DAO.ENTITY.listWithType(type.getId(), 0, 100);
    Assert.assertTrue(entities1.contains(entity));

    final Collection<Entity> entities2 = DAO.ENTITY.listWithType("", 0, 100);
    Assert.assertTrue(entities2.contains(entity));

    // DELETE
    DAO.delete(type);
    DAO.delete(entity);
  }

  /**
   * Testing PropertyValue equals and hashcode.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testPropertyValueEquals() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    final EntityType type1 = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);
    final EntityType type2 = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);

    final String propertyName = DATA_PROPERTY_NAME;
    final String propertyDescription = DATA_PROPERTY_DESC;

    final Property property1 = new Property(type1, propertyName, propertyDescription, DataType.STRING);
    final Property property2 = new Property(type2, propertyName, propertyDescription, DataType.STRING);

    final String entityName = DATA_ENTITY_NAME;

    final Entity entity1 = new Entity(type1, entityName);
    final Entity entity2 = new Entity(type2, entityName);

    final String value = "123";

    final PropertyValue propertyvalue1 = new PropertyValue(entity1, property1, value);
    final PropertyValue propertyvalue2 = new PropertyValue();
    propertyvalue2.setEntity(entity2);
    propertyvalue2.setProperty(property2);
    propertyvalue2.setValue(value);

    Assert.assertTrue(propertyvalue1.equals(propertyvalue2));
  }

  @Test
  public void testPropertyValueDataTypes() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    // CREATING DATA
    final EntityType type = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);

    final String entityName = DATA_ENTITY_NAME;
    final Entity entity = new Entity(type, entityName);

    final Property stringProperty = new Property(type, "stringProperty", "", DataType.STRING);
    final Property integerProperty = new Property(type, "integerProperty", "", DataType.INTEGER);
    final Property longProperty = new Property(type, "longProperty", "", DataType.LONG);
    final Property floatProperty = new Property(type, "floatProperty", "", DataType.FLOAT);
    final Property doubleProperty = new Property(type, "doubleProperty", "", DataType.DOUBLE);
    final Property dateProperty = new Property(type, "dateProperty", "", DataType.DATE);
    final Property uriProperty = new Property(type, "uriProperty", "", DataType.URI);
    final Property stringListProperty = new Property(type, "stringListProperty", "", DataType.STRING_LIST);
    final Property stringDictionaryProperty = new Property(type, "stringDictionaryProperty", "",
      DataType.STRING_DICTIONARY);

    final PropertyValue stringPropertyValue1 = new PropertyValue(entity, stringProperty, "This is a string");
    final PropertyValue integerPropertyValue1 = new PropertyValue(entity, integerProperty, 123);
    final PropertyValue longPropertyValue1 = new PropertyValue(entity, longProperty, 123L);
    final PropertyValue floatPropertyValue1 = new PropertyValue(entity, floatProperty, 123.1f);
    final PropertyValue doublePropertyValue1 = new PropertyValue(entity, doubleProperty, 123.1d);
    final PropertyValue datePropertyValue1 = new PropertyValue(entity, dateProperty, new Date());
    final PropertyValue uriPropertyValue1 = new PropertyValue(entity, uriProperty, URI.create("http://example.com"));
    final PropertyValue stringListPropertyValue1 = new PropertyValue(entity, stringListProperty, Arrays.asList("item1",
      "item2"));
    final PropertyValue stringDictionaryPropertyValue1 = new PropertyValue(entity, stringDictionaryProperty,
      Arrays.asList(new DictionaryItem("key1", "value1"), new DictionaryItem("key2", "value2")));

    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);
    final SourceAdaptor adaptor = new SourceAdaptor(DATA_PLUGIN_NAME, DATA_PLUGIN_VERSION,
      DATA_SOURCE_ADAPTOR_INSTANCE, source, Arrays.asList(type), Arrays.asList(stringProperty, integerProperty,
        floatProperty, doubleProperty, dateProperty, uriProperty, stringListProperty, stringDictionaryProperty),
      new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(source);
    DAO.save(adaptor);
    DAO.save(stringProperty, integerProperty, longProperty, floatProperty, doubleProperty, dateProperty, uriProperty,
      stringListProperty, stringDictionaryProperty);
    DAO.PROPERTY_VALUE.save(adaptor, stringPropertyValue1, integerPropertyValue1, longPropertyValue1,
      floatPropertyValue1, doublePropertyValue1, datePropertyValue1, uriPropertyValue1, stringListPropertyValue1,
      stringDictionaryPropertyValue1);

    // TESTING
    final PropertyValue stringPropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), stringProperty.getId());

    Assert.assertEquals(stringPropertyValue1, stringPropertyValue2);

    final PropertyValue integerPropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), integerProperty.getId());

    Assert.assertEquals(integerPropertyValue1, integerPropertyValue2);

    final PropertyValue longPropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), longProperty.getId());

    Assert.assertEquals(longPropertyValue1, longPropertyValue2);

    final PropertyValue floatPropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), floatProperty.getId());

    Assert.assertEquals(floatPropertyValue1, floatPropertyValue2);

    final PropertyValue doublePropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), doubleProperty.getId());

    Assert.assertEquals(doublePropertyValue1, doublePropertyValue2);

    final PropertyValue datePropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), dateProperty.getId());

    Assert.assertEquals(datePropertyValue1, datePropertyValue2);

    final PropertyValue uriPropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), uriProperty.getId());

    Assert.assertEquals(uriPropertyValue1, uriPropertyValue2);

    final PropertyValue stringListPropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(), stringListProperty.getId());

    Assert.assertEquals(stringListPropertyValue1, stringListPropertyValue2);

    final PropertyValue stringDictionaryPropertyValue2 = DAO.PROPERTY_VALUE.find(entity.getId(),
      stringDictionaryProperty.getId());

    Assert.assertEquals(stringDictionaryPropertyValue1, stringDictionaryPropertyValue2);

    // CLEAN UP
    DAO.delete(type);
    DAO.delete(entity);
    DAO.delete(stringProperty, integerProperty, longProperty, floatProperty, doubleProperty, dateProperty, uriProperty,
      stringListProperty, stringDictionaryProperty);
    DAO.delete(stringPropertyValue1, integerPropertyValue1, longPropertyValue1, floatPropertyValue1,
      doublePropertyValue1, datePropertyValue1, uriPropertyValue1, stringListPropertyValue1,
      stringDictionaryPropertyValue1);
  }

  /**
   * Test if data type value setting exceptions behave properly.
   * 
   * @throws InvalidJavaClassForDataTypeException
   *           Unexpected exception when setting a value which java class is not
   *           compatible with defined property data type.
   * @throws UnsupportedDataTypeException
   *           Unexpected exception of unsupported data type.
   */
  @Test
  public void testPropertyValueDataTypeExceptions() throws InvalidJavaClassForDataTypeException,
    UnsupportedDataTypeException {

    // Null property
    try {
      new PropertyValue(null, null, null);
      Assert.fail();
    } catch (final UnsupportedDataTypeException e) {
      Assert.assertNull(e.getDatatype());
    }

    // data
    final EntityType type = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);

    final String entityName = DATA_ENTITY_NAME;
    final Entity entity = new Entity(type, entityName);

    Property property;

    // java class not compatible with data type
    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.STRING);
    try {
      new PropertyValue(entity, property, 0);
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.INTEGER);
    try {
      new PropertyValue(entity, property, "0");
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.FLOAT);
    try {
      new PropertyValue(entity, property, "0");
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.DOUBLE);
    try {
      new PropertyValue(entity, property, "0");
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.URI);
    try {
      new PropertyValue(entity, property, "0");
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.DATE);
    try {
      new PropertyValue(entity, property, "0");
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.STRING_LIST);
    try {
      new PropertyValue(entity, property, "0");
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

    property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.STRING_DICTIONARY);
    try {
      new PropertyValue(entity, property, "0");
      Assert.fail();
    } catch (final InvalidJavaClassForDataTypeException e) {

    }

  }

  /**
   * Test PropertyValue CRUD operations.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testPropertyValueCRUD() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    // CREATE
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    final PropertyValue pv = new PropertyValue(entity, property, "123");
    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);
    final SourceAdaptor adaptor = new SourceAdaptor(DATA_PLUGIN_NAME, DATA_PLUGIN_VERSION,
      DATA_SOURCE_ADAPTOR_INSTANCE, source, Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);

    DAO.PROPERTY_VALUE.save(adaptor, pv);

    // List
    final Collection<PropertyValue> pvs = Jenabean.instance().reader().load(PropertyValue.class);
    Assert.assertTrue(pvs.contains(pv));

    // QUERY
    final List<PropertyValue> pvs2 = DAO.PROPERTY_VALUE.query("", 0, 100);

    Assert.assertTrue(pvs2.contains(pv));

    // FIND
    final PropertyValue pv2 = DAO.PROPERTY_VALUE.find(entity.getId(), property.getId());

    Assert.assertNotNull(pv2);
    Assert.assertEquals(pv, pv2);

    // COUNT
    final int count = DAO.PROPERTY_VALUE.count("");
    Assert.assertEquals(1, count);

    // DELETE
    DAO.delete(pv);
    DAO.delete(entity);
    DAO.delete(property);
    DAO.delete(type);

    // LIST AGAIN
    final Collection<PropertyValue> pvs3 = Jenabean.instance().reader().load(PropertyValue.class);
    Assert.assertFalse(pvs3.contains(pv));

    // QUERY AGAIN
    final List<PropertyValue> pvs4 = DAO.PROPERTY_VALUE.query("", 0, 100);
    Assert.assertFalse(pvs4.contains(pv));

    // FIND AGAIN
    final PropertyValue pv3 = DAO.PROPERTY_VALUE.find(entity.getId(), property.getId());
    Assert.assertNull(pv3);

    // COUNT AGAIN
    final int count2 = DAO.PROPERTY_VALUE.count("");
    Assert.assertEquals(0, count2);

    // Check cascade delete of measurements
    final int measurementsCount = DAO.MEASUREMENT.countByPropertyValue(pv);
    Assert.assertEquals(0, measurementsCount);
  }

  /**
   * Test PropertyValue listing methods.
   * 
   * @throws InvalidJavaClassForDataTypeException
   *           Property value uses invalid java class for its data type
   *           definition.
   * @throws UnsupportedDataTypeException
   *           The defined data type is not supported.
   */
  @Test
  public void testPropertyValueListings() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    // CREATE
    final EntityType type = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);
    final SourceAdaptor adaptor = new SourceAdaptor(DATA_PLUGIN_NAME, DATA_PLUGIN_VERSION,
      DATA_SOURCE_ADAPTOR_INSTANCE, source, Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);
    DAO.PROPERTY_VALUE.save(adaptor, pv);

    final Collection<PropertyValue> pvs1 = DAO.PROPERTY_VALUE.listWithEntity(type.getName(), entity.getName(), 0, 100);
    Assert.assertTrue(pvs1.contains(pv));

    final Collection<PropertyValue> pvs2 = DAO.PROPERTY_VALUE.listWithProperty(type.getName(), property.getName(), 0,
      100);
    Assert.assertTrue(pvs2.contains(pv));

    final Collection<PropertyValue> pvs3 = DAO.PROPERTY_VALUE.listWithEntityAndProperty(entity.getId(),
      property.getId(), 0, 100);
    Assert.assertTrue(pvs3.contains(pv));

    // DELETE
    DAO.delete(type);
    DAO.delete(entity);
    DAO.delete(property);
    DAO.delete(pv);
    DAO.delete(source);
    DAO.delete(adaptor);

  }

  @Test
  public void testPropertyValueOrphanListings() throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    // CREATE
    final EntityType type = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);
    final SourceAdaptor adaptor = new SourceAdaptor(DATA_PLUGIN_NAME, DATA_PLUGIN_VERSION,
      DATA_SOURCE_ADAPTOR_INSTANCE, source, Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);
    DAO.PROPERTY_VALUE.save(adaptor, pv);

    // Make pv an orphan
    final List<Measurement> measurements = DAO.MEASUREMENT.listByPropertyValue(pv, 0, 100);
    for (Measurement measurement : measurements) {
      measurement.delete();
    }

    // Test
    final int orphanCount = DAO.PROPERTY_VALUE.countMeasurementOrphan();
    Assert.assertEquals(1, orphanCount);

  }

  /**
   * Test {@link Measurement} DAO
   * 
   * @throws UnsupportedDataTypeException
   * @throws InvalidJavaClassForDataTypeException
   */
  @Test
  public void testMeasurements() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    // CREATE
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC, DataType.INTEGER);

    final PropertyValue pv1 = new PropertyValue(entity, property, 1);
    final PropertyValue pv2 = new PropertyValue(entity, property, 2);

    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);
    final SourceAdaptor adaptor1 = new SourceAdaptor(DATA_PLUGIN_NAME, "0.0.1", DATA_SOURCE_ADAPTOR_INSTANCE, source,
      Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());
    final SourceAdaptor adaptor2 = new SourceAdaptor(DATA_PLUGIN_NAME, "0.0.2", DATA_SOURCE_ADAPTOR_INSTANCE, source,
      Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor1, adaptor2);

    final Calendar c = Calendar.getInstance();
    c.set(2004, 1, 1);
    final Date date1 = c.getTime();
    DAO.PROPERTY_VALUE.save(adaptor1, date1, pv1);
    c.set(2005, 1, 1);
    final Date date2 = c.getTime();
    DAO.PROPERTY_VALUE.save(adaptor2, date2, pv2);

    final PropertyValue lastValue = DAO.PROPERTY_VALUE.find(entity.getId(), property.getId(), new Date());
    Assert.assertEquals(pv2, lastValue);

    c.set(2004, 6, 1);
    final PropertyValue previousValue = DAO.PROPERTY_VALUE.find(entity.getId(), property.getId(), c.getTime());
    Assert.assertEquals(pv1, previousValue);

    // FIND
    final Measurement measurement1 = DAO.MEASUREMENT.findById(property.getName(), date1);
    Assert.assertNotNull(measurement1);
    Assert.assertEquals(pv1, measurement1.getPropertyValue());
    Assert.assertEquals(adaptor1, measurement1.getAdaptor());
    Assert.assertEquals(date1, measurement1.getTimestamp());

    final Measurement measurement2 = DAO.MEASUREMENT.findById(property.getName(), date2);
    Assert.assertNotNull(measurement2);
    Assert.assertEquals(pv2, measurement2.getPropertyValue());
    Assert.assertEquals(adaptor2, measurement2.getAdaptor());
    Assert.assertEquals(date2, measurement2.getTimestamp());

    // LIST BY PROPERTY VALUE
    final List<Measurement> listByPropertyValue = DAO.MEASUREMENT.listByPropertyValue(pv1, 0, 100);
    Assert.assertTrue(listByPropertyValue.contains(measurement1));
    Assert.assertEquals(1, listByPropertyValue.size());

    final int countByPropertyValue = DAO.MEASUREMENT.countByPropertyValue(pv2);
    Assert.assertEquals(1, countByPropertyValue);

    // LIST BY ADAPTOR
    final List<Measurement> listByAdaptor = DAO.MEASUREMENT.listByAdaptor(adaptor1, 0, 100);
    Assert.assertTrue(listByAdaptor.contains(measurement1));
    Assert.assertEquals(1, listByAdaptor.size());

    final int countByAdaptor = DAO.MEASUREMENT.countByAdaptor(adaptor2);
    Assert.assertEquals(1, countByAdaptor);

    // LIST BY SOURCE
    final List<Measurement> listBySource = DAO.MEASUREMENT.listBySource(source, 0, 100);
    Assert.assertTrue(listBySource.containsAll(Arrays.asList(measurement1, measurement2)));
    Assert.assertEquals(2, listBySource.size());

    final int countBySource = DAO.MEASUREMENT.countBySource(source);
    Assert.assertEquals(2, countBySource);

    // LIST BY PROPERTY
    final List<Measurement> listByProperty = DAO.MEASUREMENT.listByProperty(property, 0, 100);
    Assert.assertTrue(listByProperty.containsAll(Arrays.asList(measurement1, measurement2)));
    Assert.assertEquals(2, listByProperty.size());

    final int countByProperty = DAO.MEASUREMENT.countByProperty(property);
    Assert.assertEquals(2, countByProperty);

    // DELETE
    DAO.delete(type);
    DAO.delete(entity);
    DAO.delete(property);
    DAO.delete(source);
    DAO.delete(adaptor1, adaptor2);
    DAO.delete(pv1, pv2);

    // Test measurements cleanup
    Assert.assertEquals(0, DAO.MEASUREMENT.countByPropertyValue(pv1));
    Assert.assertEquals(0, DAO.MEASUREMENT.countByPropertyValue(pv2));
  }

  /**
   * Test {@link Question} equals and hashcode.
   */
  @Test
  public void testQuestionEquals() {
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    final String sparql = "?s watch:entity watch-Entity:" + entity.getName() + ". ?s watch:property watch-Property:"
      + Property.createId(type.getName(), property.getName() + ". FILTER(?s < 200)");
    final RequestTarget target = RequestTarget.PROPERTY_VALUE;
    final List<EntityType> types = Arrays.asList(type);
    final List<Property> properties = Arrays.asList(property);
    final List<Entity> entities = Arrays.asList(entity);
    final long period = 30000;

    final Question question1 = new Question(sparql, target, types, properties, entities, period);
    final Question question2 = new Question();
    question2.setId(question1.getId());
    question2.setSparql(sparql);
    question2.setTarget(target);
    question2.setTypes(types);
    question2.setProperties(properties);
    question2.setEntities(entities);
    question2.setPeriod(period);

    Assert.assertEquals(question1, question2);
    Assert.assertEquals(question1.getId(), question2.getId());
    Assert.assertEquals(question1.getSparql(), question2.getSparql());
    Assert.assertEquals(question1.getTarget(), question2.getTarget());
    Assert.assertEquals(question1.getTypes(), question2.getTypes());
    Assert.assertEquals(question1.getProperties(), question2.getProperties());
    Assert.assertEquals(question1.getEntities(), question2.getEntities());
    Assert.assertEquals(question1.getPeriod(), question2.getPeriod());

  }

  /**
   * Test AsyncRequest equals and hashcode.
   */
  @Test
  public void testAsyncRequestEquals() {

    // CREATE DATA
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    type.save();
    entity.save();
    property.save();

    // CREATE ASYNC REQUEST
    final String sparql = "?s watch:entity watch-Entity:" + entity.getName() + ". ?s watch:property watch-Property:"
      + Property.createId(type.getName(), property.getName() + ". FILTER(?s < 200)");
    final RequestTarget target = RequestTarget.PROPERTY_VALUE;
    final List<EntityType> types = Arrays.asList(type);
    final List<Property> properties = Arrays.asList(property);
    final List<Entity> entities = Arrays.asList(entity);
    final long period = 30000;

    final Question question = new Question(sparql, target, types, properties, entities, period);
    final Notification notification = new Notification("test", new HashMap<String, String>());
    final List<Notification> notifications = Arrays.asList(notification);
    final Plan plan = null;

    final Trigger trigger1 = new Trigger(question, notifications, plan);
    final List<Trigger> triggers = Arrays.asList(trigger1);

    final AsyncRequest arequest1 = new AsyncRequest("test", triggers);

    // CASCADE SAVE
    final AsyncRequest arequest2 = DAO.ASYNC_REQUEST.save(arequest1);

    // Test saved
    Assert.assertTrue(arequest1.equals(arequest2));

    // Test empty constructor
    final AsyncRequest arequest3 = new AsyncRequest();
    arequest3.setDescription("test");
    arequest3.setTriggers(triggers);
    arequest3.setId(arequest1.getId());
    Assert.assertTrue(arequest1.equals(arequest3));

    // Test found
    final AsyncRequest arequest4 = DAO.ASYNC_REQUEST.findById(arequest1.getId());

    Assert.assertTrue(question.equals(arequest4.getTriggers().get(0).getQuestion()));
    Assert.assertTrue(notification.equals(arequest4.getTriggers().get(0).getNotifications().get(0)));
    Assert.assertNull(arequest4.getTriggers().get(0).getPlan());
    Assert.assertTrue(trigger1.equals(arequest4.getTriggers().get(0)));
    Assert.assertTrue(arequest1.equals(arequest4));

    // DELETE
    type.save();
    entity.save();
    property.save();

    // CASCADE DELETE
    DAO.delete(arequest1);

  }

  /**
   * Test Entity CRUD operations.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testAsyncRequestCRUD() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    // CREATE DATA
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);
    final SourceAdaptor adaptor = new SourceAdaptor(DATA_PLUGIN_NAME, DATA_PLUGIN_VERSION,
      DATA_SOURCE_ADAPTOR_INSTANCE, source, Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);
    DAO.PROPERTY_VALUE.save(adaptor, pv);

    // CREATE ASYNC REQUEST
    final String sparql = "?s watch:entity watch-Entity:" + entity.getName() + ". ?s watch:property watch-Property:"
      + Property.createId(type.getName(), property.getName() + ". FILTER(?s < 200)");
    final RequestTarget target = RequestTarget.PROPERTY_VALUE;
    final List<EntityType> types = Arrays.asList(type);
    final List<Property> properties = Arrays.asList(property);
    final List<Entity> entities = Arrays.asList(entity);
    final long period = 30000;

    final Question question = new Question(sparql, target, types, properties, entities, period);
    final Notification notification = new Notification("test", new HashMap<String, String>());
    final List<Notification> notifications = Arrays.asList(notification);
    final Plan plan = null;

    final Trigger trigger = new Trigger(question, notifications, plan);
    final List<Trigger> triggers = Arrays.asList(trigger);

    final AsyncRequest arequest = new AsyncRequest("test", triggers);

    // CASCADE SAVE
    DAO.save(arequest);

    // List
    final Collection<AsyncRequest> arequests = DAO.ASYNC_REQUEST.list(0, 100);
    Assert.assertTrue(arequests.contains(arequest));

    // QUERY
    final List<AsyncRequest> arequests2 = DAO.ASYNC_REQUEST.query("", 0, 100);
    Assert.assertTrue(arequests2.contains(arequest));

    // FIND
    final AsyncRequest arequest2 = DAO.ASYNC_REQUEST.findById(arequest.getId());

    Assert.assertNotNull(arequest2);
    Assert.assertEquals(arequest, arequest2);

    // COUNT
    final int count = DAO.ASYNC_REQUEST.count("");
    Assert.assertEquals(1, count);

    // DELETE
    type.save();
    entity.save();
    property.save();
    pv.save();

    // CASCADE DELETE
    DAO.delete(arequest);

    // LIST AGAIN
    final Collection<AsyncRequest> arequests3 = DAO.ASYNC_REQUEST.list(0, 100);
    Assert.assertFalse(arequests3.contains(arequest));

    // QUERY AGAIN
    final List<AsyncRequest> arequests4 = DAO.ASYNC_REQUEST.query("", 0, 100);
    Assert.assertFalse(arequests4.contains(arequest));

    // FIND AGAIN
    final AsyncRequest arequest3 = DAO.ASYNC_REQUEST.findById(arequest.getId());
    Assert.assertNull(arequest3);

    // COUNT AGAIN
    final int count2 = DAO.ASYNC_REQUEST.count("");
    Assert.assertEquals(0, count2);
  }

  /**
   * Test making a request.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testRequest() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    // CREATE DATA
    final EntityType type = new EntityType();
    type.setName(DATA_TYPE_NAME);
    type.setDescription(DATA_TYPE_DESC);

    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    type.save();
    entity.save();
    property.save();
    pv.save();

    // MAKE ENTITY TYPE REQUEST
    final String query1 = EntityDAO.getEntityRDFId(type.getName(), entity.getName()) + " watch:type ?s";
    final RequestTarget target1 = RequestTarget.ENTITY_TYPE;
    @SuppressWarnings("unchecked")
    final List<PropertyValue> results1 = (List<PropertyValue>) DAO.REQUEST.query(target1, query1, 0, 100);
    Assert.assertTrue(results1.contains(type));

    // MAKE PROPERTY REQUEST
    final String query2 = "?s watch:type " + EntityTypeDAO.getEntityTypeRDFId(type);
    final RequestTarget target2 = RequestTarget.PROPERTY;
    @SuppressWarnings("unchecked")
    final List<PropertyValue> results2 = (List<PropertyValue>) DAO.REQUEST.query(target2, query2, 0, 100);
    Assert.assertTrue(results2.contains(property));

    // MAKE ENTITY REQUEST
    final String query3 = "?s watch:type " + EntityTypeDAO.getEntityTypeRDFId(type);

    final RequestTarget target3 = RequestTarget.ENTITY;
    @SuppressWarnings("unchecked")
    final List<PropertyValue> results3 = (List<PropertyValue>) DAO.REQUEST.query(target3, query3, 0, 100);
    Assert.assertTrue(results3.contains(entity));

    // MAKE PROPERTY VALUE REQUEST
    final String query4 = "?s watch:entity " + EntityDAO.getEntityRDFId(type.getName(), entity.getName())
      + " . ?s watch:property " + PropertyDAO.getPropertyRDFId(type.getName(), property.getName());
    // TODO test " . FILTER(?s < 200)";
    final RequestTarget target4 = RequestTarget.PROPERTY_VALUE;

    @SuppressWarnings("unchecked")
    final List<PropertyValue> results4 = (List<PropertyValue>) DAO.REQUEST.query(target4, query4, 0, 100);
    Assert.assertTrue(results4.contains(pv));
  }

  /**
   * Test Source DAO.
   */
  @Test
  public void testSource() {
    // CREATE
    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);

    // SAVE
    DAO.save(source);

    // FIND
    final Source source2 = DAO.SOURCE.findByName(DATA_SOURCE_NAME);
    Assert.assertEquals(source, source2);

    // QUERY
    final List<Source> list = DAO.SOURCE.query("", 0, 100);
    Assert.assertTrue(list.contains(source));
    Assert.assertEquals(1, list.size());

    // COUNT
    final int count = DAO.SOURCE.count("");
    Assert.assertEquals(1, count);

    // DELETE
    DAO.delete(source);
    final List<Source> list2 = DAO.SOURCE.query("", 0, 100);
    Assert.assertFalse(list2.contains(source));
    Assert.assertEquals(0, list2.size());
  }

  /**
   * Test Source Adaptor DAO.
   */
  @Test
  public void testSourceAdaptor() {
    // CREATE
    final String sourceName1 = "source1";
    final Source source1 = new Source(sourceName1, "test source number 1");
    final String sourceName2 = "source2";
    final Source source2 = new Source(sourceName2, "test source number 2");

    final String adaptorName1 = "adaptor1";
    final String adaptorName2 = "adaptor2";
    final String adaptorVersion1 = "0.0.1";
    final String adaptorVersion2 = "0.0.2";
    final String adaptorInstance = "default";
    final SourceAdaptor adaptor1v1 = new SourceAdaptor(adaptorName1, adaptorVersion1, adaptorInstance, source1, null,
      null, null);
    final SourceAdaptor adaptor1v2 = new SourceAdaptor(adaptorName1, adaptorVersion2, adaptorInstance, source1, null,
      null, null);
    final SourceAdaptor adaptor2v1 = new SourceAdaptor(adaptorName2, adaptorVersion1, adaptorInstance, source2, null,
      null, null);

    // SAVE
    DAO.save(source1, source2);
    DAO.save(adaptor1v1, adaptor1v2, adaptor2v1);

    // FIND
    final SourceAdaptor foundAdaptor = DAO.SOURCE_ADAPTOR.findById(adaptorName1, adaptorVersion1, adaptorInstance);
    Assert.assertEquals(adaptor1v1, foundAdaptor);

    // QUERY
    final List<SourceAdaptor> list = DAO.SOURCE_ADAPTOR.query("", 0, 100);
    Assert.assertTrue(list.containsAll(Arrays.asList(adaptor1v1, adaptor1v2, adaptor2v1)));
    Assert.assertEquals(3, list.size());

    // LIST BY NAME
    final List<SourceAdaptor> listByName = DAO.SOURCE_ADAPTOR.listByName(adaptorName1, 0, 100);
    Assert.assertTrue(listByName.containsAll(Arrays.asList(adaptor1v1, adaptor1v2)));
    Assert.assertFalse(listByName.contains(adaptor2v1));
    Assert.assertEquals(2, listByName.size());

    // LIST BY SOURCE
    final List<SourceAdaptor> listBySource = DAO.SOURCE_ADAPTOR.listBySource(source2, 0, 100);
    Assert.assertTrue(listBySource.containsAll(Arrays.asList(adaptor2v1)));
    Assert.assertFalse(listBySource.contains(adaptor1v1));
    Assert.assertFalse(listBySource.contains(adaptor1v2));
    Assert.assertEquals(1, listBySource.size());

    // COUNT
    final int count = DAO.SOURCE_ADAPTOR.count("");
    Assert.assertEquals(3, count);

    // COUNT BY NAME
    final int countByName = DAO.SOURCE_ADAPTOR.countByName(adaptorName2);
    Assert.assertEquals(1, countByName);

    // COUNT BY SOURCE
    final int countBySource = DAO.SOURCE_ADAPTOR.countBySource(source1);
    Assert.assertEquals(2, countBySource);

    // DELETE
    DAO.delete(adaptor1v1, adaptor1v2, adaptor2v1);
    DAO.delete(source1, source2);
    final int count2 = DAO.SOURCE_ADAPTOR.count("");
    Assert.assertEquals(0, count2);

  }

  /**
   * Test Source Adaptor DAO active flag methods.
   */
  @Test
  public void testActiveSourceAdaptor() {
    // CREATE
    final String sourceName = "source1";
    final Source source = new Source(sourceName, "test source number 1");

    final String adaptorName = "adaptor1";
    final String adaptorVersion1 = "0.0.1";
    final String adaptorVersion2 = "0.0.2";
    final String adaptorInstance = "default";
    final SourceAdaptor adaptor1 = new SourceAdaptor(adaptorName, adaptorVersion1, adaptorInstance, source, null, null,
      null);
    final SourceAdaptor adaptor2 = new SourceAdaptor(adaptorName, adaptorVersion2, adaptorInstance, source, null, null,
      null);

    adaptor2.setActive(false);

    // SAVE
    DAO.save(source);
    DAO.save(adaptor1, adaptor2);

    // QUERY ACTIVE
    final List<SourceAdaptor> list = DAO.SOURCE_ADAPTOR.queryActive(true, 0, 100);
    Assert.assertTrue(list.containsAll(Arrays.asList(adaptor1)));
    Assert.assertEquals(1, list.size());

    // QUERY INACTIVE
    final List<SourceAdaptor> list2 = DAO.SOURCE_ADAPTOR.queryActive(false, 0, 100);
    Assert.assertTrue(list2.containsAll(Arrays.asList(adaptor2)));
    Assert.assertEquals(1, list2.size());

    // COUNT ACTIVE
    final int count1 = DAO.SOURCE_ADAPTOR.countActive(true);
    Assert.assertEquals(1, count1);

    // COUNT INACTIVE
    final int count2 = DAO.SOURCE_ADAPTOR.countActive(false);
    Assert.assertEquals(1, count2);

    // DELETE
    DAO.delete(adaptor1, adaptor2);
    DAO.delete(source);

  }

  /**
   * Test Source Adaptor delete.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testSourceAdaptorCascadeDelete() throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    final Source source = new Source(DATA_SOURCE_NAME, DATA_SOURCE_DESCRIPTION);
    final SourceAdaptor adaptor = new SourceAdaptor(DATA_PLUGIN_NAME, DATA_PLUGIN_VERSION,
      DATA_SOURCE_ADAPTOR_INSTANCE, source, null, null, null);

    final EntityType type = new EntityType(DATA_TYPE_NAME, DATA_TYPE_DESC);
    final Entity entity = new Entity(type, DATA_ENTITY_NAME);
    final Property property = new Property(type, DATA_PROPERTY_NAME, DATA_PROPERTY_DESC);
    final PropertyValue pv1 = new PropertyValue(entity, property, "123");
    final PropertyValue pv2 = new PropertyValue(entity, property, "124");

    final SourceAdaptorEvent event = new SourceAdaptorEvent(SourceAdaptorEventType.EXECUTED, "scheduled execution",
      true, "", new Date(), adaptor);

    DAO.save(source);
    DAO.save(adaptor);
    DAO.PROPERTY_VALUE.save(adaptor, pv1, pv1, pv2);
    DAO.save(event);

    // cascade delete
    DAO.delete(adaptor);

    final int countAll = DAO.SOURCE_ADAPTOR.countAll();
    Assert.assertEquals(0, countAll);

    // check if related information was deleted
    int measurementCount = DAO.MEASUREMENT.countByAdaptor(adaptor);
    Assert.assertEquals(0, measurementCount);

    final int orphanPropertiesCount = DAO.PROPERTY_VALUE.countMeasurementOrphan();
    Assert.assertEquals(0, orphanPropertiesCount);

    final int eventsCount = DAO.SOURCE_ADAPTOR_EVENT.countByAdaptor(adaptor);
    Assert.assertEquals(0, eventsCount);

  }

}
