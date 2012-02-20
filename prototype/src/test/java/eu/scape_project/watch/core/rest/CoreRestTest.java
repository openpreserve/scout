package eu.scape_project.watch.core.rest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import com.google.common.io.Files;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test of the watch core REST API.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class CoreRestTest extends JerseyTest {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(CoreRestTest.class);

  /**
   * Logging start and finish of tests.
   */
  @Rule
  public MethodRule watchman = new TestWatchman() {
    public void starting(final FrameworkMethod method) {
      LOG.info("TEST {} being run...", method.getName());
    }

    public void finished(final FrameworkMethod method) {
      LOG.info("TEST {} finished", method.getName());
    }
  };

  /**
   * Jersey web resource.
   */
  private WebResource resource;

  /**
   * Temporary directory to keep the KB data.
   */
  private static final File DATA_TEMP_DIR = Files.createTempDir();

  /**
   * Initialize data folder.
   */
  @BeforeClass
  public static void beforeClass() {
    LOG.info("Creating data folder at " + DATA_TEMP_DIR.getPath());
    KB.setDataFolder(DATA_TEMP_DIR.getPath());
  }

  /**
   * Cleanup data folder.
   */
  @AfterClass
  public static void afterClass() {
    LOG.info("Deleting data folder at " + DATA_TEMP_DIR.getPath());
    FileUtils.deleteQuietly(DATA_TEMP_DIR);
  }

  /**
   * Initialize Jersey Test Framework.
   * 
   */
  public CoreRestTest() {
    // super("eu.scape_project.watch.core.rest.resource");
    super(new WebAppDescriptor.Builder("javax.ws.rs.Application", WatchApplication.class.getName()).initParam(
      JSONConfiguration.FEATURE_POJO_MAPPING, "true").build());
  }

  @Override
  public void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();

    final ClientConfig cc = new DefaultClientConfig();
    cc.getClasses().add(JacksonJsonProvider.class);
    final Client clientWithJacksonSerializer = Client.create(cc);
    this.resource = clientWithJacksonSerializer.resource(getBaseURI());
  }

  /**
   * Test entity type with JSON output.
   * 
   * @see CoreRestTest#entityCRUD(eu.scape_project.watch.core.rest.WatchClient.Format)
   */
  @Test
  public void entityTypeCrudJson() {
    entityTypeCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test entity type with XML output.
   */
  @Test
  public void entityTypeCrudXML() {
    entityTypeCRUD(WatchClient.Format.XML);
  }

  /**
   * Test Entity Type CRUD operations.
   * 
   * @param format
   *          The output format.
   */
  public void entityTypeCRUD(final WatchClient.Format format) {
    final WatchClient client = new WatchClient(this.resource, format);

    // CREATE
    final String name = "test";
    final String description = "A test";

    final EntityType entitytype = client.createEntityType(name, description);

    System.out.println("Created: " + entitytype);

    assertEquals(entitytype.getName(), name);
    assertEquals(entitytype.getDescription(), description);

    // TODO test creating an already existing entity type

    // GET
    final EntityType entitytype3 = client.getEntityType(name);
    Assert.assertNotNull(entitytype3);
    assertEquals(entitytype, entitytype3);

    // LIST
    final List<EntityType> list = client.listEntityType();
    Assert.assertTrue(list.contains(entitytype));

    // TODO test update

    // DELETE
    final EntityType entity4 = client.deleteEntityType(name);
    Assert.assertEquals(entity4, entitytype);

    // GET
    final EntityType entitytype5 = client.getEntityType(name);
    Assert.assertNull(entitytype5);

    // LIST
    final List<EntityType> list2 = client.listEntityType();
    Assert.assertFalse(list2.contains(entitytype));
  }

  /**
   * Test Entity CRUD operations with JSON output.
   * 
   * @see #entityCRUD(eu.scape_project.watch.core.rest.WatchClient.Format)
   */
  @Test
  public void entityCrudJSON() {
    entityCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test Entity CRUD operations with XML output.
   * 
   * @see #entityCRUD(eu.scape_project.watch.core.rest.WatchClient.Format)
   */
  @Test
  public void entityCrudXML() {
    entityCRUD(WatchClient.Format.XML);
  }

  /**
   * Test {@link Entity} CRUD operations.
   * 
   * @param format
   *          the output format
   */
  public void entityCRUD(final WatchClient.Format format) {
    final WatchClient client = new WatchClient(this.resource, format);

    // CREATE
    final String typeName = "test";
    final String typeDescription = "A test";

    final EntityType entitytype = client.createEntityType(typeName, typeDescription);

    final String name = "test01";
    final Entity entity = client.createEntity(name, typeName);
    Assert.assertNotNull(entity);
    Assert.assertEquals(entity.getName(), name);
    Assert.assertNotNull(entity.getEntityType());
    Assert.assertEquals(entity.getEntityType(), entitytype);

    // TODO test creating an already existing entity

    // GET
    final Entity entity2 = client.getEntity(name);
    assertEquals(entity, entity2);

    // LIST
    final List<Entity> list = client.listEntity();
    LOG.info("entity {} in list {}?", new Object[]{entity, list});
    Assert.assertTrue(list.contains(entity));
    

    // TODO test update

    // DELETE
    final Entity entity3 = client.deleteEntity(name);
    Assert.assertEquals(entity3, entity);

    final EntityType entitytype2 = client.deleteEntityType(typeName);
    Assert.assertEquals(entitytype2, entitytype);

    // GET
    final Entity entity4 = client.getEntity(name);
    Assert.assertNull(entity4);

    // LIST
    final List<Entity> list2 = client.listEntity();
    Assert.assertFalse(list2.contains(entity));
  }

  /**
   * Test {@link Property} CRUD operations using JSON output format.
   * 
   * @see #propertyCRUD(eu.scape_project.watch.core.rest.WatchClient.Format)
   */
  @Test
  public void propertyCrudJSON() {
    propertyCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test {@link Property} CRUD operations using XML output format.
   * 
   * @see #propertyCRUD(eu.scape_project.watch.core.rest.WatchClient.Format)
   */
  @Test
  public void propertyCrudXML() {
    propertyCRUD(WatchClient.Format.XML);
  }

  /**
   * Test {@link Property} CRUD operations.
   * 
   * @param format
   *          The output format
   */
  public void propertyCRUD(final WatchClient.Format format) {
    final WatchClient client = new WatchClient(this.resource, format);

    // CREATE
    final String typeName = "test";
    final String typeDescription = "A test";

    final EntityType entitytype = client.createEntityType(typeName, typeDescription);

    final String name = "property01";
    final String description = "The property 01";
    final Property property = client.createProperty(typeName, name, description);
    Assert.assertNotNull(property);
    Assert.assertEquals(property.getName(), name);
    Assert.assertEquals(property.getDescription(), description);
    Assert.assertNotNull(property.getDatatype());
    // Assert.assertEquals(property.getDatatype(), datatype);

    // TODO test creating an already existing entity

    // GET
    final Property property2 = client.getProperty(typeName, name);
    Assert.assertNotNull(property2);
    Assert.assertEquals(property, property2);

    // LIST
    final List<Property> list = client.listProperty();
    Assert.assertTrue(list.contains(property));

    // TODO test update

    // DELETE
    final Property property3 = client.deleteProperty(typeName, name);
    Assert.assertEquals(property3, property);

    final EntityType entitytype2 = client.deleteEntityType(typeName);
    Assert.assertEquals(entitytype2, entitytype);

    // GET
    final Property property4 = client.getProperty(typeName, name);
    Assert.assertNull(property4);

    // LIST
    final List<Property> list2 = client.listProperty();
    Assert.assertFalse(list2.contains(property));
  }

  /**
   * Test {@link PropertyValue} CRUD operations using JSON output format.
   * 
   * @see #propertyValueCRUD(eu.scape_project.watch.core.rest.WatchClient.Format)
   */
  @Test
  public void propertyValueCrudJSON() {
    propertyValueCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test {@link PropertyValue} CRUD operations using XML output format.
   * 
   * @see #propertyValueCRUD(eu.scape_project.watch.core.rest.WatchClient.Format)
   */
  @Test
  public void propertyValueCrudXML() {
    propertyValueCRUD(WatchClient.Format.XML);
  }

  /**
   * Test {@link PropertyValue} CRUD operations.
   * 
   * @param format
   *          The output format
   */
  public void propertyValueCRUD(final WatchClient.Format format) {
    final WatchClient client = new WatchClient(this.resource, format);

    // CREATE
    final String typeName = "test";
    final String typeDescription = "A test";

    final EntityType entitytype = client.createEntityType(typeName, typeDescription);

    final String entityName = "test01";
    final Entity entity = client.createEntity(entityName, typeName);

    final String propertyName = "property01";
    final String propertyDescription = "The property 01";
    final Property property = client.createProperty(typeName, propertyName, propertyDescription);

    final String value = "99999";
    final PropertyValue propertyValue = client.createPropertyValue(entity.getName(), property.getName(), value);
    Assert.assertNotNull(propertyValue);
    Assert.assertEquals(propertyValue.getEntity(), entity);
    Assert.assertEquals(propertyValue.getProperty(), property);
    Assert.assertEquals(propertyValue.getValue(), value);

    // GET
    final PropertyValue propertyValue2 = client.getPropertyValue(entityName, propertyName);
    Assert.assertNotNull(propertyValue2);
    Assert.assertEquals(propertyValue, propertyValue2);

    // LIST
    final List<PropertyValue> list = client.listPropertyValue();
    Assert.assertTrue(list.contains(propertyValue));

    // TODO test update

    // DELETE
    final PropertyValue propertyValue3 = client.deletePropertyValue(entityName, propertyName);
    Assert.assertEquals(propertyValue3, propertyValue);

    final EntityType entitytype2 = client.deleteEntityType(typeName);
    Assert.assertEquals(entitytype2, entitytype);

    final Entity entity2 = client.deleteEntity(entityName);
    Assert.assertEquals(entity.getName(), entity2.getName());

    final Property property2 = client.deleteProperty(typeName, propertyName);
    Assert.assertEquals(property2, property);

    // GET
    final PropertyValue propertyValue4 = client.getPropertyValue(entityName, propertyName);
    Assert.assertNull(propertyValue4);

    // LIST
    final List<PropertyValue> list2 = client.listPropertyValue();
    Assert.assertFalse(list2.contains(propertyValue));

  }

}
