package eu.scape_project.watch.model;

import eu.scape_project.watch.dao.AsyncRequestDAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.dao.EntityTypeDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.dao.PropertyValueDAO;
import eu.scape_project.watch.dao.RequestDAO;
import eu.scape_project.watch.domain.AsyncRequest;
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

import java.io.File;
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

  @Test
  public void testEntityTypeEquals() {
    final String name = "name";
    final String description = "description";

    final EntityType type1 = new EntityType(name, description);
    final EntityType type2 = new EntityType(name, description);

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
    final List<EntityType> types2 = EntityTypeDAO.getInstance().query("", 0, 100);

    Assert.assertTrue(types2.contains(type));

    // FIND
    final EntityType type2 = EntityTypeDAO.getInstance().findById(type.getName());

    Assert.assertNotNull(type2);
    Assert.assertEquals(type, type2);

    // COUNT
    int count = EntityTypeDAO.getInstance().count("");
    Assert.assertEquals(1, count);

    // DELETE
    type.delete();

    // LIST AGAIN
    final Collection<EntityType> types3 = Jenabean.instance().reader().load(EntityType.class);

    Assert.assertFalse(types3.contains(type));

    // QUERY AGAIN
    final List<EntityType> types4 = EntityTypeDAO.getInstance().query("", 0, 100);

    Assert.assertFalse(types4.contains(type));

    // FIND AGAIN
    final EntityType type3 = EntityTypeDAO.getInstance().findById(type.getName());
    Assert.assertNull(type3);

    // COUNT AGAIN
    int count2 = EntityTypeDAO.getInstance().count("");
    Assert.assertEquals(0, count2);

  }

  /**
   * Test Entity Type CRUD listeners.
   */
  @Test
  public void testEntityTypeListeners() {

    @SuppressWarnings("unchecked")
    DOListener<EntityType> mockDOListener = Mockito.mock(DOListener.class);

    EntityTypeDAO.getInstance().addDOListener(mockDOListener);

    EntityType type1 = new EntityType("test1", "this is a test");
    EntityType type2 = new EntityType("test2", "this is another test");

    EntityTypeDAO.getInstance().save(type1, type2);
    Mockito.verify(mockDOListener).onUpdated(type1);
    Mockito.verify(mockDOListener).onUpdated(type2);

    EntityTypeDAO.getInstance().delete(type1, type2);
    Mockito.verify(mockDOListener).onRemoved(type1);
    Mockito.verify(mockDOListener).onRemoved(type2);

    EntityTypeDAO.getInstance().removeDOListener(mockDOListener);

  }

  @Test
  public void testPropertyEquals() {

    final String typename = "typename";
    final String typedescription = "typedescription";

    final EntityType type1 = new EntityType(typename, typedescription);
    final EntityType type2 = new EntityType(typename, typedescription);

    final String name = "propertyname";
    final String description = "propertydescription";

    final Property property1 = new Property(type1, name, description);
    final Property property2 = new Property(type2, name, description);

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
    final List<Property> properties2 = PropertyDAO.getInstance().query("", 0, 100);

    Assert.assertTrue(properties2.contains(property));

    // FIND
    final Property property2 = PropertyDAO.getInstance().findByEntityTypeAndName(type.getName(), property.getName());

    Assert.assertNotNull(property2);
    Assert.assertEquals(property, property2);

    // COUNT
    int count = PropertyDAO.getInstance().count("");
    Assert.assertEquals(1, count);

    // DELETE
    property.delete();
    type.delete();

    // LIST AGAIN
    final Collection<Property> properties3 = Jenabean.instance().reader().load(Property.class);

    Assert.assertFalse(properties3.contains(property));

    // QUERY AGAIN
    final List<Property> properties4 = PropertyDAO.getInstance().query("", 0, 100);

    Assert.assertFalse(properties4.contains(property));

    // FIND AGAIN
    final Property property3 = PropertyDAO.getInstance().findByEntityTypeAndName(type.getName(), property.getName());
    Assert.assertNull(property3);

    // COUNT AGAIN
    int count2 = PropertyDAO.getInstance().count("");
    Assert.assertEquals(0, count2);

  }

  @Test
  public void testPropertyListings() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    final Property property = new Property(type, "property1", "property description");

    type.save();
    property.save();

    Collection<Property> properties1 = PropertyDAO.getInstance().listWithType(type.getName(), 0, 100);
    Assert.assertTrue(properties1.contains(property));

    // DELETE
    type.delete();
    property.delete();
  }

  @Test
  public void testEntityEquals() {

    final String typename = "typename";
    final String typedescription = "typedescription";

    final EntityType type1 = new EntityType(typename, typedescription);
    final EntityType type2 = new EntityType(typename, typedescription);

    final String name = "entityname";

    final Entity entity1 = new Entity(type1, name);
    final Entity entity2 = new Entity(type2, name);

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
    final List<Entity> entities2 = EntityDAO.getInstance().query("", 0, 100);

    Assert.assertTrue(entities2.contains(entity));

    // FIND
    final Entity entity2 = EntityDAO.getInstance().findById(entity.getName());

    Assert.assertNotNull(entity2);
    Assert.assertEquals(entity, entity2);

    // COUNT
    int count = EntityDAO.getInstance().count("");
    Assert.assertEquals(1, count);

    // DELETE
    entity.delete();
    type.delete();

    // LIST AGAIN
    final Collection<Entity> entities3 = Jenabean.instance().reader().load(Entity.class);
    Assert.assertFalse(entities3.contains(entity));

    // QUERY AGAIN
    final List<Entity> entities4 = EntityDAO.getInstance().query("", 0, 100);
    Assert.assertFalse(entities4.contains(entity));

    // FIND AGAIN
    final Entity entity3 = EntityDAO.getInstance().findById(entity.getName());
    Assert.assertNull(entity3);

    // COUNT AGAIN
    int count2 = EntityDAO.getInstance().count("");
    Assert.assertEquals(0, count2);
  }

  @Test
  public void testEntityListings() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");

    type.save();
    entity.save();

    Collection<Entity> entities1 = EntityDAO.getInstance().listWithType(type.getName(), 0, 100);
    Assert.assertTrue(entities1.contains(entity));

    Collection<Entity> entities2 = EntityDAO.getInstance().listWithType("", 0, 100);
    Assert.assertTrue(entities2.contains(entity));

    // DELETE
    type.delete();
    entity.delete();
  }

  @Test
  public void testPropertyValueEquals() {

    final String typeName = "typename";
    final String typeDescription = "typedescription";

    final EntityType type1 = new EntityType(typeName, typeDescription);
    final EntityType type2 = new EntityType(typeName, typeDescription);

    final String propertyName = "propertyname";
    final String propertyDescription = "propertydescription";

    final Property property1 = new Property(type1, propertyName, propertyDescription);
    final Property property2 = new Property(type2, propertyName, propertyDescription);

    final String entityName = "entityname";

    final Entity entity1 = new Entity(type1, entityName);
    final Entity entity2 = new Entity(type2, entityName);

    final String value = "123";

    final PropertyValue propertyvalue1 = new PropertyValue(entity1, property1, value);
    final PropertyValue propertyvalue2 = new PropertyValue(entity2, property2, value);

    Assert.assertTrue(propertyvalue1.equals(propertyvalue2));
  }

  /**
   * Test PropertyValue CRUD operations.
   */
  @Test
  public void testPropertyValueCRUD() {

    // CREATE
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    // final Property propertyInteger = new Property(type, "propertyInteger",
    // "property description");
    // final Property propertyLong = new Property(type, "propertyLong",
    // "property description");
    // final Property propertyDouble = new Property(type, "propertyDouble",
    // "property description");
    // final Property propertyFloat = new Property(type, "propertyFloat",
    // "property description");
    final Property propertyString = new Property(type, "propertyString", "property description");
    // final Property propertyDate = new Property(type, "propertyDate",
    // "property description");

    // final PropertyValue pvInteger = new PropertyValue(entity,
    // propertyInteger, 123);
    // final PropertyValue pvLong = new PropertyValue(entity, propertyLong,
    // 123L);
    // final PropertyValue pvDouble = new PropertyValue(entity, propertyDouble,
    // 123D);
    // final PropertyValue pvFloat = new PropertyValue(entity, propertyFloat,
    // 123F);
    final PropertyValue pvString = new PropertyValue(entity, propertyString, "123");
    // Date now = new Date();
    // final PropertyValue pvDate = new PropertyValue(entity, propertyDate,
    // now);

    type.save();
    entity.save();
    // propertyInteger.save();
    // propertyLong.save();
    // propertyDouble.save();
    // propertyFloat.save();
    propertyString.save();
    // propertyDate.save();
    // pvInteger.save();
    // pvLong.save();
    // pvDouble.save();
    // pvFloat.save();
    pvString.save();
    // pvDate.save();

    // List
    final Collection<PropertyValue> pvs = Jenabean.instance().reader().load(PropertyValue.class);
    System.out.println(pvString);
    System.out.println(pvs);
    // Assert.assertTrue(pvs.contains(pvInteger));
    // Assert.assertTrue(pvs.contains(pvLong));
    // Assert.assertTrue(pvs.contains(pvDouble));
    // Assert.assertTrue(pvs.contains(pvFloat));
    Assert.assertTrue(pvs.contains(pvString));
    // Assert.assertTrue(pvs.contains(pvDate));

    // QUERY
    final List<PropertyValue> pvs2 = PropertyValueDAO.getInstance().query("", 0, 100);

    // Assert.assertTrue(pvs2.contains(pvInteger));
    // Assert.assertTrue(pvs.contains(pvLong));
    // Assert.assertTrue(pvs.contains(pvDouble));
    // Assert.assertTrue(pvs.contains(pvFloat));
    Assert.assertTrue(pvs.contains(pvString));
    // Assert.assertTrue(pvs.contains(pvDate));

    // FIND
    final PropertyValue pvString2 = PropertyValueDAO.getInstance().findByEntityAndName(entity.getName(),
      propertyString.getName());

    Assert.assertNotNull(pvString2);
    Assert.assertEquals(pvString, pvString2);

    // TODO find other types

    // COUNT
    int count = PropertyValueDAO.getInstance().count("");
    Assert.assertEquals(1, count);

    // DELETE
    // pvInteger.delete();
    // pvLong.delete();
    // pvDouble.delete();
    // pvFloat.delete();
    pvString.delete();
    // pvDate.delete();
    entity.delete();
    // propertyInteger.delete();
    // propertyLong.delete();
    // propertyDouble.delete();
    // propertyFloat.delete();
    propertyString.delete();
    // propertyDate.delete();
    type.delete();

    // LIST AGAIN
    final Collection<PropertyValue> pvs3 = Jenabean.instance().reader().load(PropertyValue.class);
    Assert.assertFalse(pvs3.contains(pvString));

    // QUERY AGAIN
    final List<PropertyValue> pvs4 = PropertyValueDAO.getInstance().query("", 0, 100);
    Assert.assertFalse(pvs4.contains(pvString));

    // FIND AGAIN
    final PropertyValue pvInteger3 = PropertyValueDAO.getInstance().findByEntityAndName(entity.getName(),
      propertyString.getName());
    Assert.assertNull(pvInteger3);

    // TODO test other types

    // COUNT AGAIN
    int count2 = PropertyValueDAO.getInstance().count("");
    Assert.assertEquals(0, count2);
  }

  @Test
  public void testPropertyValueListings() {
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

    Collection<PropertyValue> pvs1 = PropertyValueDAO.getInstance().listWithEntity(entity.getName(), 0, 100);
    Assert.assertTrue(pvs1.contains(pv));

    Collection<PropertyValue> pvs2 = PropertyValueDAO.getInstance().listWithProperty(type.getName(),
      property.getName(), 0, 100);
    Assert.assertTrue(pvs2.contains(pv));

    Collection<PropertyValue> pvs3 = PropertyValueDAO.getInstance().listWithEntityAndProperty(entity.getName(),
      type.getName(), property.getName(), 0, 100);
    Assert.assertTrue(pvs3.contains(pv));

    // DELETE
    type.delete();
    entity.delete();
    property.delete();
    pv.delete();

  }

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
    String sparql = "?s watch:entity watch-Entity:" + entity.getName() + ". ?s watch:property watch-Property:"
      + Property.createId(type.getName(), property.getName() + ". FILTER(?s < 200)");
    RequestTarget target = RequestTarget.PROPERTY_VALUE;
    List<EntityType> types = Arrays.asList(type);
    List<Property> properties = Arrays.asList(property);
    List<Entity> entities = Arrays.asList(entity);
    long period = 30000;

    Question question = new Question(sparql, target, types, properties, entities, period);
    Notification notification = new Notification("test", new HashMap<String, String>());
    List<Notification> notifications = Arrays.asList(notification);
    Plan plan = null;

    Trigger trigger1 = new Trigger(question, notifications, plan);
    List<Trigger> triggers = Arrays.asList(trigger1);

    AsyncRequest arequest1 = new AsyncRequest(triggers);

    // CASCADE SAVE
    AsyncRequest arequest2 = AsyncRequestDAO.getInstance().save(arequest1);

    // Test saved
    Assert.assertTrue(arequest1.equals(arequest2));

    // Test found
    final AsyncRequest arequest3 = AsyncRequestDAO.getInstance().findById(arequest1.getId());

    Assert.assertTrue(question.equals(arequest3.getTriggers().get(0).getQuestion()));
    Assert.assertTrue(notification.equals(arequest3.getTriggers().get(0).getNotifications().get(0)));
    Assert.assertNull(arequest3.getTriggers().get(0).getPlan());
    Assert.assertTrue(trigger1.equals(arequest3.getTriggers().get(0)));
    Assert.assertTrue(arequest1.equals(arequest3));

    // DELETE
    type.save();
    entity.save();
    property.save();

    // CASCADE DELETE
    AsyncRequestDAO.getInstance().delete(arequest1);

  }

  /**
   * Test Entity CRUD operations.
   */
  @Test
  public void testAsyncRequestCRUD() {

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
    String sparql = "?s watch:entity watch-Entity:" + entity.getName() + ". ?s watch:property watch-Property:"
      + Property.createId(type.getName(), property.getName() + ". FILTER(?s < 200)");
    RequestTarget target = RequestTarget.PROPERTY_VALUE;
    List<EntityType> types = Arrays.asList(type);
    List<Property> properties = Arrays.asList(property);
    List<Entity> entities = Arrays.asList(entity);
    long period = 30000;

    Question question = new Question(sparql, target, types, properties, entities, period);
    Notification notification = new Notification("test", new HashMap<String, String>());
    List<Notification> notifications = Arrays.asList(notification);
    Plan plan = null;

    Trigger trigger = new Trigger(question, notifications, plan);
    List<Trigger> triggers = Arrays.asList(trigger);

    AsyncRequest arequest = new AsyncRequest(triggers);

    // CASCADE SAVE
    AsyncRequestDAO.getInstance().save(arequest);

    // List
    final Collection<AsyncRequest> arequests = AsyncRequestDAO.getInstance().list(0, 100);
    Assert.assertTrue(arequests.contains(arequest));

    // QUERY
    final List<AsyncRequest> arequests2 = AsyncRequestDAO.getInstance().query("", 0, 100);
    Assert.assertTrue(arequests2.contains(arequest));

    // FIND
    final AsyncRequest arequest2 = AsyncRequestDAO.getInstance().findById(arequest.getId());

    Assert.assertNotNull(arequest2);
    Assert.assertEquals(arequest, arequest2);

    // COUNT
    int count = AsyncRequestDAO.getInstance().count("");
    Assert.assertEquals(1, count);

    // DELETE
    type.save();
    entity.save();
    property.save();
    pv.save();

    // CASCADE DELETE
    AsyncRequestDAO.getInstance().delete(arequest);

    // LIST AGAIN
    final Collection<AsyncRequest> arequests3 = AsyncRequestDAO.getInstance().list(0, 100);
    Assert.assertFalse(arequests3.contains(arequest));

    // QUERY AGAIN
    final List<AsyncRequest> arequests4 = AsyncRequestDAO.getInstance().query("", 0, 100);
    Assert.assertFalse(arequests4.contains(arequest));

    // FIND AGAIN
    final AsyncRequest arequest3 = AsyncRequestDAO.getInstance().findById(arequest.getId());
    Assert.assertNull(arequest3);

    // COUNT AGAIN
    int count2 = AsyncRequestDAO.getInstance().count("");
    Assert.assertEquals(0, count2);
  }

  @Test
  public void testAsyncRequestListeners() {

    @SuppressWarnings("unchecked")
    DOListener<AsyncRequest> mockListener = Mockito.mock(DOListener.class);
    AsyncRequestDAO.getInstance().addDOListener(mockListener);
    AsyncRequest request = new AsyncRequest();
    AsyncRequestDAO.getInstance().save(request);
    Mockito.verify(mockListener).onUpdated(request);

  }

  @Test
  public void testRequest() {

    // CREATE DATA
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");
    final Property property2 = new Property(type, "property2", "property description");

    final PropertyValue pv = new PropertyValue(entity, property, "image/tiff");
    final PropertyValue pv2 = new PropertyValue(entity, property2, "123");

    type.save();
    entity.save();
    property.save();
    property2.save();
    pv.save();
    pv2.save();

    // MAKE ENTITY TYPE REQUEST
    String query1 = EntityDAO.getEntityRDFId(entity.getName()) + " watch:type ?s";
    RequestTarget target1 = RequestTarget.ENTITY_TYPE;
    @SuppressWarnings("unchecked")
    List<PropertyValue> results1 = (List<PropertyValue>) RequestDAO.getInstance().query(target1, query1, 0, 100);
    Assert.assertTrue(results1.contains(type));

    // MAKE PROPERTY REQUEST
    String query2 = "?s watch:type watch-EntityType:" + type.getName();
    RequestTarget target2 = RequestTarget.PROPERTY;
    @SuppressWarnings("unchecked")
    List<PropertyValue> results2 = (List<PropertyValue>) RequestDAO.getInstance().query(target2, query2, 0, 100);
    Assert.assertTrue(results2.contains(property));

    // MAKE ENTITY REQUEST
    String query3 = "?s watch:type watch-EntityType:" + type.getName();
    RequestTarget target3 = RequestTarget.ENTITY;
    @SuppressWarnings("unchecked")
    List<PropertyValue> results3 = (List<PropertyValue>) RequestDAO.getInstance().query(target3, query3, 0, 100);
    Assert.assertTrue(results3.contains(entity));

    // MAKE REGEX VALUE REQUEST
    String query4 = "?s watch:entity " + EntityDAO.getEntityRDFId(entity.getName()) + " . ?s watch:property "
      + PropertyDAO.getPropertyRDFId(type.getName(), property.getName())
      + " . ?s watch:value ?value . FILTER regex(?value, 'image.*', 'i')";
    RequestTarget target4 = RequestTarget.PROPERTY_VALUE;

    @SuppressWarnings("unchecked")
    List<PropertyValue> results4 = (List<PropertyValue>) RequestDAO.getInstance().query(target4, query4, 0, 100);
    Assert.assertTrue(results4.contains(pv));

    // MAKE LESSER THAN VALUE REQUEST
    // TODO test filter in other types e.g. FILTER (?s < 200)
    // String query5 = "?s watch:entity " +
    // EntityDAO.getEntityRDFId(entity.getName()) + " . ?s watch:property "
    // + PropertyDAO.getPropertyRDFId(type.getName(), property2.getName())
    // + " . ?s watch:value ?value . FILTER (?value < 200)";
    //
    // RequestTarget target5 = RequestTarget.PROPERTY_VALUE;
    //
    // @SuppressWarnings("unchecked")
    // List<PropertyValue> results5 = (List<PropertyValue>)
    // RequestDAO.getInstance().query(target5, query5, 0, 100);
    // Assert.assertTrue(results5.contains(pv2));
  }

}
