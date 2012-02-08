package eu.scape_project.watch.core;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import thewebsemantic.Sparql;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.VCARD;

import eu.scape_project.watch.core.model.EntityType;

public class KBTest {
	Logger LOG = Logger.getLogger(KBTest.class.getSimpleName());

	@Test
	public void shouldTestDefaultModel() throws Exception {
		Dataset dataset = KB.getInstance().getDataset();
		Model model = dataset.getDefaultModel();
		String url = "http://whatever.test/JohnSmith";
		String name = "John Smith";
		Resource resource = model.createResource(url);
		resource.addProperty(VCARD.FN, name);

		StmtIterator iterator = model.listStatements();

		Assert.assertTrue(iterator.hasNext());

		Statement statement = iterator.nextStatement();
		Resource subject = statement.getSubject();
		Property predicate = statement.getPredicate();
		RDFNode object = statement.getObject();

		Assert.assertEquals(url, subject.getURI());
		Assert.assertEquals(VCARD.FN.getURI(), predicate.getURI());
		Assert.assertEquals(name, object.toString());

		Assert.assertFalse(iterator.hasNext());

	}

	@Test
	public void testEntityType() {
		KB.getInstance();

		// CREATE
		EntityType type = new EntityType();
		type.setName("tests");
		type.setDescription("Test entities");

		type.save();

		printStatements();

		// List
		Collection<EntityType> types = KB.getInstance().getReader()
				.load(EntityType.class);

		Assert.assertTrue(types.contains(type));

		// QUERY
		String query = "SELECT ?s WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://watch.scape-project.eu/EntityType> }";
		List<EntityType> types3 = Sparql.exec(KB.getInstance().getModel(),
				EntityType.class, query);

		Assert.assertTrue(types3.contains(type));

		// DELETE
		type.delete();

		// LIST AGAIN
		Collection<EntityType> types2 = KB.getInstance().getReader()
				.load(EntityType.class);

		Assert.assertFalse(types2.contains(type));

		// QUERY AGAIN
		List<EntityType> types4 = Sparql.exec(KB.getInstance().getModel(),
				EntityType.class, query);

		Assert.assertFalse(types4.contains(type));

	}

	private void printStatements() {

		StmtIterator statements = KB.getInstance().getModel().listStatements();
		try {
			while (statements.hasNext()) {
				Statement stmt = statements.next();
				Resource s = stmt.getSubject();
				Resource p = stmt.getPredicate();
				RDFNode o = stmt.getObject();

				String sinfo = "";
				String pinfo = "";
				String oinfo = "";

				if (s.isURIResource()) {
					sinfo = "URI " + s.getURI();
				} else if (s.isAnon()) {
					sinfo = "Anon " + s.getId();
				}

				if (p.isURIResource())
					pinfo = "URI " + p.getURI();

				if (o.isURIResource()) {
					oinfo = "URI " + o.toString();
				} else if (o.isAnon()) {
					oinfo = "Anon " + o.toString();
				} else if (o.isLiteral()) {
					oinfo = "Lit " + o.asLiteral();
				}
				LOG.info("TRIPLE (" + sinfo + ", " + pinfo + ", " + oinfo);
			}
		} catch (Throwable e) {
			LOG.info(e.getMessage());
		} finally {
			statements.close();
		}
	}
}
