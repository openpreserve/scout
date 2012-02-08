package eu.scape_project.watch.core.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;

public class CoreRestTest extends JerseyTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(CoreRestTest.class);

	private WebResource resource;

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

	// @BeforeClass
	public static void before() {

		try {
			LOG.info("creating 4store instance");
			Process exec = Runtime.getRuntime().exec(
					"./4store-testing.sh restart");
			exec.waitFor();
		} catch (IOException e) {
			Assert.fail(e.getMessage());

		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
		}
	}

	/*
	 * Uncomment the annotation to remove the 4store kb and undeploy 4store
	 * after all tests finish. Otherwise this will be done upon next test run.
	 */
	// @AfterClass
	public static void after() {
		try {
			LOG.info("tearing down 4store instance");
			Process exec = Runtime.getRuntime()
					.exec("./4store-testing.sh stop");
			exec.waitFor();
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
		}
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
		// WatchClient client = new WatchClient(resource, "json");
		WatchClient client = new WatchClient(resource);

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
