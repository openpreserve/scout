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

	/*
	 * @Test public void entityXML() { WebResource resource = resource();
	 * 
	 * // CREATE String name = "test"; Entity entity = new Entity();
	 * entity.setName(name);
	 * 
	 * Entity entity2 =
	 * resource.path("entity.xml/").accept(MediaType.APPLICATION_XML)
	 * .post(Entity.class, entity);
	 * 
	 * System.out.println("Created: " + entity2);
	 * 
	 * assertEquals(entity.getName(), entity2.getName());
	 * 
	 * // TODO test creating an already existing entity
	 * 
	 * // GET Entity entity3 = resource.path("entity.xml/" + name)
	 * .accept(MediaType.APPLICATION_XML).get(Entity.class);
	 * 
	 * assertEquals(entity.getName(), entity3.getName());
	 * 
	 * // LIST //
	 * resource.path("entity.xml/list").accept(MediaType.APPLICATION_XML) //
	 * .get(.class);
	 * 
	 * // DELETE resource.path("entity.xml/" +
	 * name).accept(MediaType.APPLICATION_XML) .delete(Entity.class);
	 * 
	 * assertEquals(true, true);
	 * 
	 * }
	 */

	@Test
	public void entityTypeJSON() {
		WatchClient client = new WatchClient(resource, WatchClient.Format.JSON);

		// CREATE
		String name = "tests";
		String description = "Several kinds of tests";

		EntityType entitytype = client.createEntityType(name, description);

		System.out.println("Created: " + entitytype);

		assertEquals(entitytype.getName(), name);
		assertEquals(entitytype.getDescription(), description);

		// TODO test creating an already existing entity type

		// GET
		EntityType entitytype3 = client.getEntityType(name);
		// System.out.println("get: " + entitytype + " == " + entitytype3 +
		// "?");
		assertEquals(entitytype, entitytype3);

		// LIST
		List<EntityType> list = client.listEntityType();
		Assert.assertTrue(list.contains(entitytype));
		Assert.assertTrue(list.contains(entitytype3));

		// TODO test update

		// DELETE
		EntityType entity4 = client.deleteEntityType(name);
		Assert.assertEquals(entity4, entitytype);
	}

	@Test
	public void entityTypeXML() {
		WatchClient client = new WatchClient(resource, WatchClient.Format.XML);

		// CREATE
		String name = "tests";
		String description = "Several kinds of tests";

		EntityType entitytype = client.createEntityType(name, description);

		System.out.println("Created: " + entitytype);

		assertEquals(entitytype.getName(), name);
		assertEquals(entitytype.getDescription(), description);

		// TODO test creating an already existing entity type

		// GET
		EntityType entitytype3 = client.getEntityType(name);
		// System.out.println("get: " + entitytype + " == " + entitytype3 +
		// "?");
		assertEquals(entitytype, entitytype3);

		// LIST
		List<EntityType> list = client.listEntityType();
		Assert.assertTrue(list.contains(entitytype));
		Assert.assertTrue(list.contains(entitytype3));

		// TODO test update

		// DELETE
		EntityType entity4 = client.deleteEntityType(name);
		Assert.assertEquals(entity4, entitytype);
	}

	//@Test
	public void entityJSON() {
		// WatchClient client = new WatchClient(resource, "json");
		WatchClient client = new WatchClient(resource);

		// CREATE
		String name = "test";
		Entity entity = new Entity();
		entity.setName(name);

		Entity entity2 = client.createEntity(entity);

		System.out.println("Created: " + entity2);

		assertEquals(entity.getName(), entity2.getName());

		// TODO test creating an already existing entity

		// GET
		Entity entity3 = client.getEntity(name);
		assertEquals(entity.getName(), entity3.getName());

		// LIST
		List<Entity> list = client.listEntity();
		Assert.assertTrue(list.contains(entity));
		Assert.assertTrue(list.contains(entity2));
		Assert.assertTrue(list.contains(entity3));

		// TODO test update

		// DELETE
		Entity entity4 = client.deleteEntity(name);
		Assert.assertEquals(entity4, entity);
	}

}
