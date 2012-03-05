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

public class C3POProfileReader {
  
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
      
    } catch (DocumentException e) {
      e.printStackTrace();
    }
    
    this.doc = null;
  }
  
  public String getCollectionName() {
    Element collection = this.getCollectionElement();
    Attribute attribute = collection.attribute("name");
    
    return attribute.getValue();
  }
  
  public String getObjectsCount() {
    Element collection = this.getCollectionElement();
    Attribute attribute = collection.attribute("elements");
    
    return attribute.getValue();
  }
  
  private Element getCollectionElement() {
    if (this.doc == null) {
      return null;
    }
    
    Element root = doc.getRootElement();
    Element collection = (Element) root.elementIterator().next();
    return collection;
  }
}
