package eu.scape_project.watch.model;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thewebsemantic.binding.Jenabean;

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
  private static final Logger LOG = LoggerFactory.getLogger(KBTest.class.getSimpleName());

  /**
   * A temporary directory to hold the data.
   */
  private static final String DATA_TEMP_DIR = "/tmp/watch";

  /**
   * Initialize the data folder.
   */
  @BeforeClass
  public static void beforeClass() {
    final String datafolder = DATA_TEMP_DIR;
    final boolean initdata = false;
    KBUtils.dbConnect(datafolder, initdata);
  }

  /**
   * Cleanup the data folder.
   */
  @AfterClass
  public static void afterClass() {
    LOG.info("Deleting data folder at " + DATA_TEMP_DIR);
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(new File(DATA_TEMP_DIR));
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

    type.save();

    // List
    final Collection<EntityType> types = Jenabean.instance().reader().load(EntityType.class);

    Assert.assertTrue(types.contains(type));

    // QUERY
    final List<EntityType> types2 = DAO.ENTITY_TYPE.query("", 0, 100);

    Assert.assertTrue(types2.contains(type));

    // FIND
    final EntityType type2 = DAO.ENTITY_TYPE.findById(type.getName());

    Assert.assertNotNull(type2);
    Assert.assertEquals(type, type2);

    // COUNT
    final int count = DAO.ENTITY_TYPE.count("");
    Assert.assertEquals(1, count);

    // DELETE
    type.delete();

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

    final EntityType type1 = new EntityType("test1", "this is a test");
    final EntityType type2 = new EntityType("test2", "this is another test");

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

    final String typename = "typename";
    final String typedescription = "typedescription";

    final EntityType type1 = new EntityType(typename, typedescription);
    final EntityType type2 = new EntityType(typename, typedescription);

    final String name = "propertyname";
    final String description = "propertydescription";

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
    type.setName("tests");
    type.setDescription("Test entities");

    final Property property = new Property(type, "property1", "property description");

    type.save();
    property.save();

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
    property.delete();
    type.delete();

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
    type.setName("tests");
    type.setDescription("Test entities");

    final Property property = new Property(type, "property1", "property description");

    type.save();
    property.save();

    final Collection<Property> properties1 = DAO.PROPERTY.listWithType(type.getName(), 0, 100);
    Assert.assertTrue(properties1.contains(property));

    // DELETE
    type.delete();
    property.delete();
  }

  /**
   * Testing Entity equals and hashcode.
   */
  @Test
  public void testEntityEquals() {

    final String typename = "typename";
    final String typedescription = "typedescription";

    final EntityType type1 = new EntityType(typename, typedescription);
    final EntityType type2 = new EntityType(typename, typedescription);

    final String name = "entityname";

    final Entity entity1 = new Entity(type1, name);
    final Entity entity2 = new Entity();
    entity2.setEntityType(type2);
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
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");

    type.save();
    entity.save();

    // List
    final Collection<Entity> entities = Jenabean.instance().reader().load(Entity.class);
    Assert.assertTrue(entities.contains(entity));

    // QUERY
    final List<Entity> entities2 = DAO.ENTITY.query("", 0, 100);

    Assert.assertTrue(entities2.contains(entity));

    // FIND
    final Entity entity2 = DAO.ENTITY.findById(entity.getName());

    Assert.assertNotNull(entity2);
    Assert.assertEquals(entity, entity2);

    // COUNT
    final int count = DAO.ENTITY.count("");
    Assert.assertEquals(1, count);

    // DELETE
    entity.delete();
    type.delete();

    // LIST AGAIN
    final Collection<Entity> entities3 = Jenabean.instance().reader().load(Entity.class);
    Assert.assertFalse(entities3.contains(entity));

    // QUERY AGAIN
    final List<Entity> entities4 = DAO.ENTITY.query("", 0, 100);
    Assert.assertFalse(entities4.contains(entity));

    // FIND AGAIN
    final Entity entity3 = DAO.ENTITY.findById(entity.getName());
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
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");

    type.save();
    entity.save();

    final Collection<Entity> entities1 = DAO.ENTITY.listWithType(type.getName(), 0, 100);
    Assert.assertTrue(entities1.contains(entity));

    final Collection<Entity> entities2 = DAO.ENTITY.listWithType("", 0, 100);
    Assert.assertTrue(entities2.contains(entity));

    // DELETE
    type.delete();
    entity.delete();
  }

  /**
   * Testing PropertyValue equals and hashcode.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testPropertyValueEquals() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    final String typeName = "typename";
    final String typeDescription = "typedescription";

    final EntityType type1 = new EntityType(typeName, typeDescription);
    final EntityType type2 = new EntityType(typeName, typeDescription);

    final String propertyName = "propertyname";
    final String propertyDescription = "propertydescription";

    final Property property1 = new Property(type1, propertyName, propertyDescription, DataType.STRING);
    final Property property2 = new Property(type2, propertyName, propertyDescription, DataType.STRING);

    final String entityName = "entityname";

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
    final String typeName = "typename";
    final String typeDescription = "typedescription";

    final EntityType type = new EntityType(typeName, typeDescription);

    final String entityName = "entityname";
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

    type.save();
    entity.save();
    DAO.save(stringProperty, integerProperty, longProperty, floatProperty, doubleProperty, dateProperty, uriProperty,
      stringListProperty, stringDictionaryProperty);
    DAO.save(stringPropertyValue1, integerPropertyValue1, longPropertyValue1, floatPropertyValue1,
      doublePropertyValue1, datePropertyValue1, uriPropertyValue1, stringListPropertyValue1,
      stringDictionaryPropertyValue1);

    // TESTING
    final PropertyValue stringPropertyValue2 = DAO.PROPERTY_VALUE.findByEntityAndName(entity.getName(),
      stringProperty.getName());

    Assert.assertEquals(stringPropertyValue1, stringPropertyValue2);

    final PropertyValue integerPropertyValue2 = DAO.PROPERTY_VALUE.findByEntityAndName(entity.getName(),
      integerProperty.getName());

    Assert.assertEquals(integerPropertyValue1, integerPropertyValue2);

    // CLEAN UP
    type.delete();
    entity.delete();
    DAO.delete(stringProperty, integerProperty, longProperty, floatProperty, doubleProperty, dateProperty, uriProperty,
      stringListProperty, stringDictionaryProperty);
    DAO.delete(stringPropertyValue1, integerPropertyValue1, longPropertyValue1, floatPropertyValue1,
      doublePropertyValue1, datePropertyValue1, uriPropertyValue1, stringListPropertyValue1,
      stringDictionaryPropertyValue1);
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
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    type.save();
    entity.save();
    property.save();
    pv.save();

    // List
    final Collection<PropertyValue> pvs = Jenabean.instance().reader().load(PropertyValue.class);
    Assert.assertTrue(pvs.contains(pv));

    // QUERY
    final List<PropertyValue> pvs2 = DAO.PROPERTY_VALUE.query("", 0, 100);

    Assert.assertTrue(pvs2.contains(pv));

    // FIND
    final PropertyValue pv2 = DAO.PROPERTY_VALUE.findByEntityAndName(entity.getName(), property.getName());

    Assert.assertNotNull(pv2);
    Assert.assertEquals(pv, pv2);

    // COUNT
    final int count = DAO.PROPERTY_VALUE.count("");
    Assert.assertEquals(1, count);

    // DELETE
    pv.delete();
    entity.delete();
    property.delete();
    type.delete();

    // LIST AGAIN
    final Collection<PropertyValue> pvs3 = Jenabean.instance().reader().load(PropertyValue.class);
    Assert.assertFalse(pvs3.contains(pv));

    // QUERY AGAIN
    final List<PropertyValue> pvs4 = DAO.PROPERTY_VALUE.query("", 0, 100);
    Assert.assertFalse(pvs4.contains(pv));

    // FIND AGAIN
    final PropertyValue pv3 = DAO.PROPERTY_VALUE.findByEntityAndName(entity.getName(), property.getName());
    Assert.assertNull(pv3);

    // COUNT AGAIN
    final int count2 = DAO.PROPERTY_VALUE.count("");
    Assert.assertEquals(0, count2);
  }

  /**
   * Test PropertyValue listing methods.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testPropertyValueListings() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    // CREATE
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    type.save();
    entity.save();
    property.save();
    pv.save();

    final Collection<PropertyValue> pvs1 = DAO.PROPERTY_VALUE.listWithEntity(entity.getName(), 0, 100);
    Assert.assertTrue(pvs1.contains(pv));

    final Collection<PropertyValue> pvs2 = DAO.PROPERTY_VALUE.listWithProperty(type.getName(), property.getName(), 0,
      100);
    Assert.assertTrue(pvs2.contains(pv));

    final Collection<PropertyValue> pvs3 = DAO.PROPERTY_VALUE.listWithEntityAndProperty(entity.getName(),
      type.getName(), property.getName(), 0, 100);
    Assert.assertTrue(pvs3.contains(pv));

    // DELETE
    type.delete();
    entity.delete();
    property.delete();
    pv.delete();

  }

  /**
   * Test {@link Question} equals and hashcode.
   */
  @Test
  public void testQuestionEquals() {
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");

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
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");

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

    final AsyncRequest arequest1 = new AsyncRequest(triggers);

    // CASCADE SAVE
    final AsyncRequest arequest2 = DAO.ASYNC_REQUEST.save(arequest1);

    // Test saved
    Assert.assertTrue(arequest1.equals(arequest2));

    // Test empty constructor
    final AsyncRequest arequest3 = new AsyncRequest();
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
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    type.save();
    entity.save();
    property.save();
    pv.save();

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

    final AsyncRequest arequest = new AsyncRequest(triggers);

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
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");

    final PropertyValue pv = new PropertyValue(entity, property, "123");

    type.save();
    entity.save();
    property.save();
    pv.save();

    // MAKE ENTITY TYPE REQUEST
    final String query1 = EntityDAO.getEntityRDFId(entity.getName()) + " watch:type ?s";
    final RequestTarget target1 = RequestTarget.ENTITY_TYPE;
    @SuppressWarnings("unchecked")
    final List<PropertyValue> results1 = (List<PropertyValue>) DAO.REQUEST.query(target1, query1, 0, 100);
    Assert.assertTrue(results1.contains(type));

    // MAKE PROPERTY REQUEST
    final String query2 = "?s watch:type watch-EntityType:" + type.getName();
    final RequestTarget target2 = RequestTarget.PROPERTY;
    @SuppressWarnings("unchecked")
    final List<PropertyValue> results2 = (List<PropertyValue>) DAO.REQUEST.query(target2, query2, 0, 100);
    Assert.assertTrue(results2.contains(property));

    // MAKE ENTITY REQUEST
    final String query3 = "?s watch:type watch-EntityType:" + type.getName();
    final RequestTarget target3 = RequestTarget.ENTITY;
    @SuppressWarnings("unchecked")
    final List<PropertyValue> results3 = (List<PropertyValue>) DAO.REQUEST.query(target3, query3, 0, 100);
    Assert.assertTrue(results3.contains(entity));

    // MAKE PROPERTY VALUE REQUEST
    final String query4 = "?s watch:entity " + EntityDAO.getEntityRDFId(entity.getName()) + " . ?s watch:property "
      + PropertyDAO.getPropertyRDFId(type.getName(), property.getName());
    // TODO test " . FILTER(?s < 200)";
    final RequestTarget target4 = RequestTarget.PROPERTY_VALUE;

    @SuppressWarnings("unchecked")
    final List<PropertyValue> results4 = (List<PropertyValue>) DAO.REQUEST.query(target4, query4, 0, 100);
    Assert.assertTrue(results4.contains(pv));
  }

}
