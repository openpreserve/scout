package eu.scape_project.watch.rest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * Unit tests of the watch core REST API.
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
  private static final String DATA_TEMP_DIR = "/tmp/watch";

  /**
   * Initialize the data folder.
   */
  @BeforeClass
  public static void beforeClass() {
    final ConfigUtils conf = new ConfigUtils();
    final String datafolder = conf.getStringProperty(ConfigUtils.KB_DATA_FOLDER_KEY);
    final boolean initdata = conf.getBooleanProperty(ConfigUtils.KB_INSERT_TEST_DATA);
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
   * Initialize Jersey Test Framework.
   * 
   */
  public CoreRestTest() {
    // super("eu.scape_project.watch.rest.resource");
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
   * @see CoreRestTest#entityCRUD(eu.scape_project.watch.rest.WatchClient.Format)
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
   * @see #entityCRUD(eu.scape_project.watch.rest.WatchClient.Format)
   */
  @Test
  public void entityCrudJSON() {
    entityCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test Entity CRUD operations with XML output.
   * 
   * @see #entityCRUD(eu.scape_project.watch.rest.WatchClient.Format)
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
    final List<Entity> list = client.listEntity(0, 100);
    LOG.info("entity {} in list {}?", new Object[] {entity, list});
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
    final List<Entity> list2 = client.listEntity(0, 100);
    Assert.assertFalse(list2.contains(entity));
  }

  /**
   * Test {@link Property} CRUD operations using JSON output format.
   * 
   * @see #propertyCRUD(eu.scape_project.watch.rest.WatchClient.Format)
   */
  @Test
  public void propertyCrudJSON() {
    propertyCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test {@link Property} CRUD operations using XML output format.
   * 
   * @see #propertyCRUD(eu.scape_project.watch.rest.WatchClient.Format)
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
    Assert.assertEquals(property, property3);

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
   * @throws UnsupportedDataTypeException
   * @throws InvalidJavaClassForDataTypeException
   * 
   * @see #propertyValueCRUD(eu.scape_project.watch.rest.WatchClient.Format)
   */
  @Test
  public void propertyValueCrudJSON() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    propertyValueCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test {@link PropertyValue} CRUD operations using XML output format.
   * 
   * @throws UnsupportedDataTypeException
   * @throws InvalidJavaClassForDataTypeException
   * 
   * @see #propertyValueCRUD(eu.scape_project.watch.rest.WatchClient.Format)
   */
  @Test
  public void propertyValueCrudXML() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    propertyValueCRUD(WatchClient.Format.XML);
  }

  /**
   * Test {@link PropertyValue} CRUD operations.
   * 
   * @param format
   *          The output format
   * @throws UnsupportedDataTypeException
   * @throws InvalidJavaClassForDataTypeException
   */
  public void propertyValueCRUD(final WatchClient.Format format) throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
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

    final String sourceAdaptorInstance = "default";
    final SourceAdaptor sourceAdaptor = client.createSourceAdaptor("test", "0.1", sourceAdaptorInstance, "test");

    final PropertyValue propertyValue = client.createPropertyValue(sourceAdaptorInstance, new PropertyValue(entity,
      property, value));
    Assert.assertNotNull(propertyValue);
    Assert.assertEquals(propertyValue.getEntity(), entity);
    Assert.assertEquals(propertyValue.getProperty(), property);
    Assert.assertEquals(propertyValue.getValue(), value);

    // GET
    Property property2 = client.getProperty(typeName, propertyName);

    final PropertyValue propertyValue2 = client.getPropertyValue(typeName, entityName, propertyName);
    Assert.assertNotNull(propertyValue2);
    Assert.assertEquals(propertyValue, propertyValue2);

    // LIST
    final List<PropertyValue> list = client.listPropertyValue();
    Assert.assertTrue(list.contains(propertyValue));

    // TODO test update

    // DELETE
    final PropertyValue propertyValue3 = client.deletePropertyValue(typeName, entityName, propertyName);
    Assert.assertEquals(propertyValue3, propertyValue);

    final Entity entity2 = client.deleteEntity(entityName);
    Assert.assertEquals(entity.getName(), entity2.getName());

    property2 = client.deleteProperty(typeName, propertyName);
    Assert.assertEquals(property, property2);

    final EntityType entitytype2 = client.deleteEntityType(typeName);
    Assert.assertEquals(entitytype2, entitytype);

    // GET
    final PropertyValue propertyValue4 = client.getPropertyValue(typeName, entityName, propertyName);
    Assert.assertNull(propertyValue4);

    // LIST
    final List<PropertyValue> list2 = client.listPropertyValue();
    Assert.assertFalse(list2.contains(propertyValue));

  }

  /**
   * Tests on synchronous requests with XML output.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void synRequestXML() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    syncRequest(WatchClient.Format.XML);
  }

  /**
   * Tests on synchronous requests with JSON output.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void synRequestJSON() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    syncRequest(WatchClient.Format.JSON);
  }

  /**
   * Tests on synchronous requests.
   * 
   * @param format
   *          The format of the output.
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  public void syncRequest(final WatchClient.Format format) throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    final WatchClient client = new WatchClient(this.resource, format);

    // CREATE DATA
    final String typeName = "test";
    final String typeDescription = "A test";

    final EntityType entitytype = client.createEntityType(typeName, typeDescription);

    final String entityName = "test01";
    final Entity entity = client.createEntity(entityName, typeName);

    final String propertyName = "property01";
    final String propertyDescription = "The property 01";
    final Property property = client.createProperty(typeName, propertyName, propertyDescription);

    final String value = "99999";

    final String sourceAdaptorInstance = "default";
    final SourceAdaptor sourceAdaptor = client.createSourceAdaptor("test", "0.1", sourceAdaptorInstance, "test");
    final PropertyValue propertyValue = client.createPropertyValue(sourceAdaptorInstance, new PropertyValue(entity,
      property, value));

    // DO TESTS
    final List<EntityType> typeList = client.getRequest(EntityType.class, EntityDAO.getEntityRDFId(entityName)
      + " watch:type ?s", 0, 100);

    Assert.assertTrue(typeList.contains(entitytype));

    final List<PropertyValue> pvList = client.getRequest(PropertyValue.class,
      "?s watch:entity " + EntityDAO.getEntityRDFId(entityName), 0, 100);

    Assert.assertTrue(pvList.contains(propertyValue));

  }

  /**
   * Tests on asynchronous requests with XML output.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void asynRequestXML() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    asyncRequest(WatchClient.Format.XML);
  }

  /**
   * Tests on asynchronous requests with JSON output.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void asynRequestJSON() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    asyncRequest(WatchClient.Format.JSON);
  }

  /**
   * Tests on asynchronous requests.
   * 
   * @param format
   *          The format of the output.
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  public void asyncRequest(final WatchClient.Format format) throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    final WatchClient client = new WatchClient(this.resource, format);

    // CREATE DATA
    final String typeName = "test";
    final String typeDescription = "A test";

    final EntityType entitytype = client.createEntityType(typeName, typeDescription);

    final String entityName = "test01";
    final Entity entity = client.createEntity(entityName, typeName);

    final String propertyName = "property01";
    final String propertyDescription = "The property 01";
    final Property property = client.createProperty(typeName, propertyName, propertyDescription);

    final String value = "99999";

    final String sourceAdaptorInstance = "default";
    final SourceAdaptor sourceAdaptor = client.createSourceAdaptor("test", "0.1", sourceAdaptorInstance, "test");

    final PropertyValue propertyValue = client.createPropertyValue(sourceAdaptorInstance, new PropertyValue(entity,
      property, value));

    // TESTS

    // CREATE
    final Question q = new Question("?s watch:entity watch-Entity:" + entityName
      + ". ?s watch:property watch-Property:" + PropertyDAO.getPropertyRDFId(typeName, propertyName)
      + " . ?s watch:value ?v . FILTER (?v >= 100000)", RequestTarget.PROPERTY_VALUE, Arrays.asList(entitytype),
      Arrays.asList(property), Arrays.asList(entity), 60000L);
    final Notification n = new Notification("log", Arrays.asList(new DictionaryItem("recepients",
      "test@scape-project.eu")));
    final Trigger trigger = new Trigger(q, Arrays.asList(n), null);
    final AsyncRequest areq = new AsyncRequest(Arrays.asList(trigger));
    final AsyncRequest areq2 = client.createAsyncRequest(areq);
    Assert.assertEquals(areq, areq2);

    // GET

  }

  // TODO test plugin listings.

  /**
   * Tests on source adaptor requests with XML output.
   */
  @Test
  public void sourceAdaptorXML() {
    sourceAdaptor(WatchClient.Format.XML);
  }

  /**
   * Tests on source adaptor requests with JSON output.
   */
  @Test
  public void sourceAdaptorJSON() {
    sourceAdaptor(WatchClient.Format.JSON);
  }

  public void sourceAdaptor(final WatchClient.Format format) {
    final WatchClient client = new WatchClient(this.resource, format);

    // Empty start
    final List<SourceAdaptor> adaptors = client.listSourceAdaptors(null);
    LOG.info("Adaptors: {}", adaptors);
    Assert.assertEquals(0, adaptors.size());

    // TODO point to loaded plug-in
    final String pluginName = "test";
    final String pluginVersion = "0.1";

    final String instance = "default";

    // Testing non-existing source
    client.createSourceAdaptor(pluginName, pluginVersion, instance, "sourceThatDoesNotExist");

    final Source source = new Source("test", "testing source");

    // TODO client.createSource

    // TODO client.createSourceAdaptor(pluginName, pluginVersion, instance,
    // sourceName)

  }
}
