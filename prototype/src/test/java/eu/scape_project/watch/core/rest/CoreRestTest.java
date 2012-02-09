package eu.scape_project.watch.core.rest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class CoreRestTest extends JerseyTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(CoreRestTest.class);

	private WebResource resource;

	private final static File tempDir = Files.createTempDir();

	@BeforeClass
	public static void beforeClass() {
		LOG.info("Creating data folder at " + tempDir.getPath());
		KB.setDataFolder(tempDir.getPath());
	}

	@AfterClass
	public static void afterClass() {
		LOG.info("Deleting data folder at " + tempDir.getPath());
		FileUtils.deleteQuietly(tempDir);
	}

	public CoreRestTest() throws Exception {
		// super("eu.scape_project.watch.core.rest.resource");
		super(new WebAppDescriptor.Builder("javax.ws.rs.Application",
				WatchApplication.class.getName()).initParam(
				JSONConfiguration.FEATURE_POJO_MAPPING, "true").build());
	}

	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();

		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(JacksonJsonProvider.class);
		Client clientWithJacksonSerializer = Client.create(cc);
		resource = clientWithJacksonSerializer.resource(getBaseURI());
	}

	@Test
	public void entityType_CRUD_JSON() {
		entityType_CRUD(WatchClient.Format.JSON);
	}

	@Test
	public void entityType_CRUD_XML() {
		entityType_CRUD(WatchClient.Format.XML);
	}

	public void entityType_CRUD(WatchClient.Format format) {
		WatchClient client = new WatchClient(resource, format);

		// CREATE
		String name = "test";
		String description = "A test";

		EntityType entitytype = client.createEntityType(name, description);

		System.out.println("Created: " + entitytype);

		assertEquals(entitytype.getName(), name);
		assertEquals(entitytype.getDescription(), description);

		// TODO test creating an already existing entity type

		// GET
		EntityType entitytype3 = client.getEntityType(name);
		Assert.assertNotNull(entitytype3);
		assertEquals(entitytype, entitytype3);

		// LIST
		List<EntityType> list = client.listEntityType();
		Assert.assertTrue(list.contains(entitytype));

		// TODO test update

		// DELETE
		EntityType entity4 = client.deleteEntityType(name);
		Assert.assertEquals(entity4, entitytype);

		// GET
		EntityType entitytype5 = client.getEntityType(name);
		Assert.assertNull(entitytype5);

		// LIST
		List<EntityType> list2 = client.listEntityType();
		Assert.assertFalse(list2.contains(entitytype));
	}

	@Test
	public void entity_CRUD_JSON() {
		entity_CRUD(WatchClient.Format.JSON);
	}

	@Test
	public void entity_CRUD_XML() {
		entity_CRUD(WatchClient.Format.XML);
	}

	public void entity_CRUD(WatchClient.Format format) {
		WatchClient client = new WatchClient(resource, format);

		// CREATE
		String typeName = "test";
		String typeDescription = "A test";

		EntityType entitytype = client.createEntityType(typeName,
				typeDescription);

		String name = "test01";
		Entity entity = client.createEntity(name, typeName);
		Assert.assertNotNull(entity);
		Assert.assertEquals(entity.getName(), name);
		Assert.assertNotNull(entity.getEntityType());
		Assert.assertEquals(entity.getEntityType(), entitytype);

		// TODO test creating an already existing entity

		// GET
		Entity entity2 = client.getEntity(name);
		assertEquals(entity, entity2);

		// LIST
		List<Entity> list = client.listEntity();
		Assert.assertTrue(list.contains(entity));

		// TODO test update

		// DELETE
		Entity entity3 = client.deleteEntity(name);
		Assert.assertEquals(entity3, entity);

		EntityType entitytype2 = client.deleteEntityType(typeName);
		Assert.assertEquals(entitytype2, entitytype);

		// GET
		Entity entity4 = client.getEntity(name);
		Assert.assertNull(entity4);

		// LIST
		List<Entity> list2 = client.listEntity();
		Assert.assertFalse(list2.contains(entity));
	}

	@Test
	public void property_CRUD_JSON() {
		property_CRUD(WatchClient.Format.JSON);
	}

	@Test
	public void property_CRUD_XML() {
		property_CRUD(WatchClient.Format.XML);
	}

	public void property_CRUD(WatchClient.Format format) {
		WatchClient client = new WatchClient(resource, format);

		// CREATE
		String typeName = "test";
		String typeDescription = "A test";

		EntityType entitytype = client.createEntityType(typeName,
				typeDescription);

		String name = "property01";
		String description = "The property 01";
		Property property = client.createProperty(typeName, name, description);
		Assert.assertNotNull(property);
		Assert.assertEquals(property.getName(), name);
		Assert.assertEquals(property.getDescription(), description);
		// Assert.assertNotNull(property.getDatatype());
		// Assert.assertEquals(property.getDatatype(), datatype);

		// TODO test creating an already existing entity

		// GET
		Property property2 = client.getProperty(typeName, name);
		Assert.assertEquals(property, property2);

		// LIST
		List<Property> list = client.listProperty();
		Assert.assertTrue(list.contains(property));

		// TODO test update

		// DELETE
		Property property3 = client.deleteProperty(typeName, name);
		Assert.assertEquals(property3, property);

		// GET
		Property property4 = client.getProperty(typeName, name);
		Assert.assertNull(property4);

		// LIST
		List<Property> list2 = client.listProperty();
		Assert.assertTrue(list2.contains(property));
	}

}
