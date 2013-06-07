package eu.scape_project.watch.adaptor.c3po.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The profile reader for the current c3po content profile.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ProductionProfileStrategy implements ProfileVersionReader {

  /**
   * The default logger of this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ProductionProfileStrategy.class);

  /**
   * The xml document that is parsed.
   */
  private Document doc;

  @Override
  public String getCollectionName() {
    return this.getDoc().getRootElement().attributeValue("collection");
  }

  @Override
  public String getObjectsCount() {
    return this.getDoc().getRootElement().element("partition").attributeValue("count");
  }

  @Override
  public String getCollectionSize() {
    final Element size = this.getPropertyElement("size");
    String result = MISSING_VALUE;
    if (size != null) {
      result = size.attributeValue("sum");
    }

    return result;
  }

  @Override
  public String getObjectsMaxSize() {
    final Element size = this.getPropertyElement("size");
    String result = MISSING_VALUE;
    if (size != null) {
      result = size.attributeValue("max");
    }

    return result;
  }

  @Override
  public String getObjectsMinSize() {
    final Element size = this.getPropertyElement("size");
    String result = MISSING_VALUE;
    if (size != null) {
      result = size.attributeValue("min");
    }

    return result;
  }

  @Override
  public String getObjectsAvgSize() {
    final Element size = this.getPropertyElement("size");
    String result = MISSING_VALUE;
    if (size != null) {
      result = size.attributeValue("avg");
    }

    return result;
  }

  @Override
  public Map<String, String> getDistribution(final String name) {
    final Element property = this.getPropertyElement(name);
    
    if(property == null) {
      return null;
    }
    
    final Map<String, String> distribution = new HashMap<String, String>();
    final List<?> items = property.elements();

    if (items.isEmpty()) {
      return null;
    }

    for (Object o : items) {
      final Element e = (Element) o;
      final String key = e.attributeValue("id");
      final String value = e.attributeValue("value");
      distribution.put(key, value);
    }

    return distribution;

  }

  @Override
  public void setStream(final InputStream is) {
    this.getDocument(is);

  }

  private Element getPropertyElement(final String name) {
    final Element root = this.getDoc().getRootElement();
    final List<?> nodes = root.element("partition").element("properties").elements();
    
    for (Object o : nodes) {
      Element e = (Element) o;
      if (e.attributeValue("id").equals(name)) {
        return e;
      }
    }

    return null;
  }

  /**
   * Retrieves the xml document out of the input stream.
   * 
   * @param is
   *          the input stream to read.
   */
  private void getDocument(final InputStream is) {
    try {
      final SAXReader reader = new SAXReader();
      this.doc = reader.read(is);
      
    } catch (final DocumentException e) {
      LOG.error("An error occurred while reading the profile: {}", e.getMessage());
      this.doc = null;
    }

    try {
      is.close();
    } catch (final IOException e) {
      LOG.error("An error occurred while closing the input stream: {}", e.getMessage());
    }
  }
  
  private Document getDoc() {
    if (this.doc == null) {
      return DocumentHelper.createDocument();
    }
    
    return doc;
  }

}
