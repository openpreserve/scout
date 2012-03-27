package eu.scape_project.watch.model;

import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.dao.EntityTypeDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.dao.PropertyValueDAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.KBUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
   * Test Entity CRUD operations.
   */
  @Test
  public void testPropertyValueCRUD() {

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
    final List<PropertyValue> pvs2 = PropertyValueDAO.getInstance().query("", 0, 100);

    Assert.assertTrue(pvs2.contains(pv));

    // FIND
    final PropertyValue pv2 = PropertyValueDAO.getInstance().findByEntityAndName(entity.getName(), property.getName());

    Assert.assertNotNull(pv2);
    Assert.assertEquals(pv, pv2);

    // COUNT
    int count = PropertyValueDAO.getInstance().count("");
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
    final List<PropertyValue> pvs4 = PropertyValueDAO.getInstance().query("", 0, 100);
    Assert.assertFalse(pvs4.contains(pv));

    // FIND AGAIN
    final PropertyValue pv3 = PropertyValueDAO.getInstance().findByEntityAndName(entity.getName(), property.getName());
    Assert.assertNull(pv3);

    // COUNT AGAIN
    int count2 = PropertyValueDAO.getInstance().count("");
    Assert.assertEquals(0, count2);
  }

}
