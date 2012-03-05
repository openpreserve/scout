package eu.scape_project.watch.components.adaptors.c3po;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C3POProfileReader {

  protected static final String MISSING_VALUE = "cp_missing_value";

  private static final Logger LOG = LoggerFactory.getLogger(C3POProfileReader.class);

  private InputStream is;

  private Document doc;

  public C3POProfileReader(File f) throws FileNotFoundException {
    this(new FileInputStream(f));
  }

  public C3POProfileReader(InputStream is) {
    this.is = is;
    this.getDocument();
  }

  private void getDocument() {
    try {
      SAXReader reader = new SAXReader();
      this.doc = reader.read(is);
      // System.out.println(doc.asXML());

    } catch (DocumentException e) {
      e.printStackTrace();
      this.doc = null;
    }
  }

  public String getCollectionName() {
    Element collection = this.getCollectionElement();
    return this.getAttributeValue(collection, "name");
  }

  public String getObjectsCount() {
    Element collection = this.getCollectionElement();
    return this.getAttributeValue(collection, "elements");
  }
  
  public Statistics getStatistics(String property) {
    Element collection = this.getCollectionElement();
    Element properties = collection.element("properties");
    return null;
  }

  private Element getCollectionElement() {
    if (this.doc == null) {
      return null;
    }

    Element root = doc.getRootElement();
    Element collection = (Element) root.elementIterator().next();

    return collection;
  }

  private String getAttributeValue(Element collection, String name) {
    if (collection != null) {
      Attribute attribute = collection.attribute(name);
      return attribute.getValue();
    }

    return MISSING_VALUE;
  }
}
