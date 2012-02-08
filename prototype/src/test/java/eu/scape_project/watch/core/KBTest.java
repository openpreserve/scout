package eu.scape_project.watch.core;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.VCARD;

public class KBTest {

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
}
