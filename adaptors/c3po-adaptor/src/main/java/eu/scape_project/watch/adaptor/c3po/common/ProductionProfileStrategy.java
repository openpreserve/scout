package eu.scape_project.watch.adaptor.c3po.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductionProfileStrategy implements ProfileVersionReader {

  private static final Logger LOG = LoggerFactory.getLogger(ProductionProfileStrategy.class);

  private Document doc;

  @Override
  public String getCollectionName() {
    return this.doc.getRootElement().attributeValue("collection");
  }

  @Override
  public String getObjectsCount() {
    return this.doc.getRootElement().element("partition").attributeValue("count");
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
  public Map<String, String> getDistribution(String name) {
    final Element property = this.getPropertyElement(name);
    final Map<String, String> distribution = new HashMap<String, String>();
    final List items = property.selectNodes("//properties/property[@id='" + name + "']/*");

    if (items.isEmpty()) {
      return null;
    }

    for (Object o : items) {
      final Element e = (Element) o;
      final String key = e.attributeValue("value");
      final String value = e.attributeValue("count");
      distribution.put(key, value);
    }

    return distribution;

  }

  @Override
  public void setStream(InputStream is) {
    this.getDocument(is);

  }

  private Element getPropertyElement(String name) {
    final Element root = this.doc.getRootElement();
    final List nodes = root.selectNodes("//properties/property[@id='" + name + "']");

    Element e = null;

    if (nodes.size() == 1) {
      e = (Element) nodes.get(0);
    }

    return e;
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

}