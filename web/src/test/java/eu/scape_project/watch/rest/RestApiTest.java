package eu.scape_project.watch.rest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * Unit tests of the watch core REST API.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class RestApiTest extends JerseyTest {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RestApiTest.class);

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
  private static File dataTempDir;

  /**
   * Initialize the data folder.
   * 
   * @throws IOException
   *           Error creating temporary directory
   */
  @Before
  public void before() throws IOException {

    dataTempDir = JavaUtils.createTempDirectory();

    // final ConfigUtils conf = new ConfigUtils();
    // final String datafolder =
    // conf.getStringProperty(ConfigUtils.KB_DATA_FOLDER_KEY);
    // final boolean initdata =
    // conf.getBooleanProperty(ConfigUtils.KB_INSERT_TEST_DATA);
    KBUtils.dbConnect(dataTempDir.getPath(), false);
    PluginManager.getDefaultPluginManager().getConfig().override(ConfigUtils.DEFAULT_CONFIG);
  }

  /**
   * Cleanup the data folder.
   */
  @After
  public void after() {
    LOG.info("Deleting data folder at " + dataTempDir);
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempDir);
  }

  /**
   * Initialize Jersey Test Framework.
   * 
   */
  public RestApiTest() {
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
   * @see RestApiTest#entityCRUD(eu.scape_project.watch.rest.WatchClient.Format)
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
    final List<EntityType> list = client.listEntityType(0, 100);
    Assert.assertTrue(list.contains(entitytype));

    // TODO test update

    // DELETE
    final EntityType entity4 = client.deleteEntityType(name);
    Assert.assertEquals(entity4, entitytype);

    // GET
    final EntityType entitytype5 = client.getEntityType(name);
    Assert.assertNull(entitytype5);

    // LIST
    final List<EntityType> list2 = client.listEntityType(0, 100);
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
    final Entity entity = client.createEntity(entitytype, name);
    Assert.assertNotNull(entity);
    Assert.assertEquals(entity.getName(), name);
    Assert.assertNotNull(entity.getType());
    Assert.assertEquals(entity.getType(), entitytype);

    // TODO test creating an already existing entity

    // GET
    final Entity entity2 = client.getEntity(typeName, name);
    assertEquals(entity, entity2);

    // LIST
    final List<Entity> list = client.listEntity(0, 100);
    LOG.info("entity {} in list {}?", new Object[] {entity, list});
    Assert.assertTrue(list.contains(entity));

    // TODO test update

    // DELETE
    final Entity entity3 = client.deleteEntity(typeName, name);
    Assert.assertEquals(entity3, entity);

    final EntityType entitytype2 = client.deleteEntityType(typeName);
    Assert.assertEquals(entitytype2, entitytype);

    // GET
    final Entity entity4 = client.getEntity(typeName, name);
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
    final Property property = client.createProperty(entitytype, name, description);
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
    final Entity entity = client.createEntity(entitytype, entityName);

    final String propertyName = "property01";
    final String propertyDescription = "The property 01";
    final Property property = client.createProperty(entitytype, propertyName, propertyDescription);

    final String value = "99999";

    final Source source = client.createSource(new Source("test", "test source"));
    final SourceAdaptor sourceAdaptor = client.createSourceAdaptor("TestAdaptor", "0.1", "default", source.getName());

    final PropertyValue pv = new PropertyValue(entity, property, value);
    LOG.debug("Creating Property Value {}", pv);
    final PropertyValue propertyValue = client.createPropertyValue(sourceAdaptor.getInstance(), pv);
    Assert.assertNotNull(propertyValue);
    Assert.assertEquals(entity, propertyValue.getEntity());
    Assert.assertEquals(property, propertyValue.getProperty());
    Assert.assertEquals(value, propertyValue.getValue());

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
    final PropertyValue propertyValue3 = client.deletePropertyValue(propertyValue.getId());
    Assert.assertEquals(propertyValue3, propertyValue);

    final Entity entity2 = client.deleteEntity(typeName, entityName);
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
    final Entity entity = client.createEntity(entitytype, entityName);

    final String propertyName = "property01";
    final String propertyDescription = "The property 01";
    final Property property = client.createProperty(entitytype, propertyName, propertyDescription);

    final String value = "99999";

    final Source source = client.createSource(new Source("test", "test source"));
    final SourceAdaptor sourceAdaptor = client.createSourceAdaptor("TestAdaptor", "0.1", "default", source.getName());
    final PropertyValue propertyValue = client.createPropertyValue(sourceAdaptor.getInstance(), new PropertyValue(
      entity, property, value));

    // DO TESTS
    final List<EntityType> typeList = client.getRequest(EntityType.class, EntityDAO.getEntityRDFId(entity)
      + " watch:type ?s", 0, 100);

    Assert.assertTrue(typeList.contains(entitytype));

    final List<PropertyValue> pvList = client.getRequest(PropertyValue.class,
      "?s watch:entity " + EntityDAO.getEntityRDFId(entity), 0, 100);

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
    final Entity entity = client.createEntity(entitytype, entityName);

    final String propertyName = "property01";
    final String propertyDescription = "The property 01";
    final Property property = client.createProperty(entitytype, propertyName, propertyDescription);

    final String value = "99999";

    final Source source = client.createSource(new Source("test", "test source"));
    final SourceAdaptor sourceAdaptor = client.createSourceAdaptor("TestAdaptor", "0.1", "default", source.getName());

    final PropertyValue propertyValue = client.createPropertyValue(sourceAdaptor.getInstance(), new PropertyValue(
      entity, property, value));

    // TESTS

    // CREATE
    final Question q = new Question("?s watch:entity watch-Entity:" + entityName
      + ". ?s watch:property watch-Property:" + PropertyDAO.getPropertyRDFId(typeName, propertyName)
      + " . ?s watch:value ?v . FILTER (?v >= 100000)", RequestTarget.PROPERTY_VALUE, Arrays.asList(entitytype),
      Arrays.asList(property), Arrays.asList(entity), 60000L);
    final Notification n = new Notification("log", Arrays.asList(new DictionaryItem("recepients",
      "test@scape-project.eu")));
    final Trigger trigger = new Trigger(q, Arrays.asList(n), null);
    final AsyncRequest areq = new AsyncRequest("test", Arrays.asList(trigger));
    final AsyncRequest areq2 = client.createAsyncRequest(areq);
    Assert.assertEquals(areq, areq2);

    // GET

  }

  // TODO test plugin listings.

  /**
   * Test source with JSON output.
   * 
   * @see RestApiTest#sourceCRUD(eu.scape_project.watch.rest.WatchClient.Format)
   */
  @Test
  public void sourceCrudJson() {
    sourceCRUD(WatchClient.Format.JSON);
  }

  /**
   * Test source with XML output.
   */
  @Test
  public void sourceCrudXML() {
    sourceCRUD(WatchClient.Format.XML);
  }

  /**
   * Test Source CRUD operations.
   * 
   * @param format
   *          The output format.
   */
  public void sourceCRUD(final WatchClient.Format format) {
    final WatchClient client = new WatchClient(this.resource, format);

    // CREATE
    final String name = "test";
    final String description = "A test";

    final Source source = client.createSource(new Source(name, description));

    System.out.println("Created: " + source);

    assertEquals(source.getName(), name);
    assertEquals(source.getDescription(), description);

    // TODO test creating an already existing source

    // GET
    final Source source3 = client.getSource(name);
    Assert.assertNotNull(source3);
    assertEquals(source, source3);

    // LIST
    final List<Source> list = client.listSources();
    Assert.assertTrue(list.contains(source));

    // TODO test update

    // DELETE
    final Source source4 = client.deleteSource(name);
    Assert.assertEquals(source4, source);

    // GET
    final Source source5 = client.getSource(name);
    Assert.assertNull(source5);

    // LIST
    final List<Source> list2 = client.listSources();
    Assert.assertFalse(list2.contains(source));
  }

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
    final String pluginName = "TestAdaptor";
    final String pluginVersion = "0.1";

    final String instance = "default";

    // Testing non-existing source
    final SourceAdaptor adaptor = client.createSourceAdaptor(pluginName, pluginVersion, instance,
      "sourceThatDoesNotExist");
    Assert.assertNull(adaptor);

    final List<SourceAdaptor> adaptors2 = client.listSourceAdaptors(null);
    LOG.info("Adaptors: {}", adaptors2);
    Assert.assertEquals(0, adaptors2.size());

    // Create
    final Source source = client.createSource(new Source("test", "testing source"));
    final SourceAdaptor adaptor2 = client.createSourceAdaptor(pluginName, pluginVersion, instance, source.getName());
    Assert.assertNotNull(adaptor2);
    Assert.assertEquals(pluginName, adaptor2.getName());
    Assert.assertEquals(pluginVersion, adaptor2.getVersion());
    Assert.assertEquals(instance, adaptor2.getInstance());
    Assert.assertEquals(source, adaptor2.getSource());

    // Get
    final SourceAdaptor adaptor3 = client.getSourceAdaptor(adaptor2.getInstance());
    Assert.assertEquals(adaptor2, adaptor3);

    // List
    final List<SourceAdaptor> sourceAdaptors = client.listSourceAdaptors(true);
    Assert.assertEquals(1, sourceAdaptors.size());
    Assert.assertTrue(sourceAdaptors.contains(adaptor2));

    // Update
    adaptor2.setActive(false);
    final SourceAdaptor adaptor4 = client.updateSourceAdaptor(adaptor2);
    Assert.assertEquals(false, adaptor4.isActive());

    final List<SourceAdaptor> sourceAdaptors2 = client.listSourceAdaptors(true);
    Assert.assertEquals(0, sourceAdaptors2.size());

    final List<SourceAdaptor> sourceAdaptors3 = client.listSourceAdaptors(false);
    Assert.assertEquals(1, sourceAdaptors3.size());
    Assert.assertTrue(sourceAdaptors3.contains(adaptor2));

    // TODO Delete

  }
}
