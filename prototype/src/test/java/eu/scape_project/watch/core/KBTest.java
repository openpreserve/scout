package eu.scape_project.watch.core;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import thewebsemantic.Sparql;

import com.google.common.io.Files;

import eu.scape_project.watch.core.dao.EntityTypeDAO;
import eu.scape_project.watch.core.model.EntityType;

public class KBTest {
	private static final Logger LOG = Logger.getLogger(KBTest.class
			.getSimpleName());

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

	// @Test
	// public void shouldTestDefaultModel() throws Exception {
	// Dataset dataset = KB.getInstance().getDataset();
	// Model model = dataset.getDefaultModel();
	// String url = "http://whatever.test/JohnSmith";
	// String name = "John Smith";
	// Resource resource = model.createResource(url);
	// resource.addProperty(VCARD.FN, name);
	//
	// StmtIterator iterator = model.listStatements();
	//
	// Assert.assertTrue(iterator.hasNext());
	//
	// Statement statement = iterator.nextStatement();
	// Resource subject = statement.getSubject();
	// Property predicate = statement.getPredicate();
	// RDFNode object = statement.getObject();
	//
	// Assert.assertEquals(url, subject.getURI());
	// Assert.assertEquals(VCARD.FN.getURI(), predicate.getURI());
	// Assert.assertEquals(name, object.toString());
	//
	// Assert.assertFalse(iterator.hasNext());
	//
	// }

	@Test
	public void testEntityType() {
		KB.getInstance();

		// CREATE
		EntityType type = new EntityType();
		type.setName("tests");
		type.setDescription("Test entities");

		type.save();

		KBUtils.printStatements();

		// List
		Collection<EntityType> types = KB.getInstance().getReader()
				.load(EntityType.class);

		Assert.assertTrue(types.contains(type));

		// QUERY
		String query = "SELECT ?s WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://watch.scape-project.eu/EntityType> }";
		List<EntityType> types2 = Sparql.exec(KB.getInstance().getModel(),
				EntityType.class, query);

		Assert.assertTrue(types2.contains(type));

		// FIND
		EntityType type2 = EntityTypeDAO.findById(type.getName());

		Assert.assertNotNull(type2);
		Assert.assertEquals(type, type2);

		// DELETE
		type.delete();

		// LIST AGAIN
		Collection<EntityType> types3 = KB.getInstance().getReader()
				.load(EntityType.class);

		Assert.assertFalse(types3.contains(type));

		// QUERY AGAIN
		List<EntityType> types4 = Sparql.exec(KB.getInstance().getModel(),
				EntityType.class, query);

		Assert.assertFalse(types4.contains(type));

		// FIND AGAIN
		EntityType type3 = EntityTypeDAO.findById(type.getName());
		Assert.assertNull(type3);

	}

}
