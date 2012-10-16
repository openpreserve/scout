package eu.scape_project.watch.adaptor.c3po.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple response parser for the current version of c3po.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class C3POResponseParser {

  /**
   * A default logger for this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DummyReader.class);

  /**
   * The xml document representation of the profile.
   */
  private Document doc;

  /**
   * Gets the collection identifiers from the response in the input stream.
   * 
   * @param response
   *          the response in the form of a input stream.
   * @return a list with the collection identifiers, or and empty list.
   */
  public List<String> getCollectionsFromResponse(final InputStream response) {
    this.getDocument(response);
    if (this.doc == null) {
      return null;
    }
    
    final List<?> nodes = this.doc.getRootElement().selectNodes("collection");
    final List<String> result = new ArrayList<String>();

    for (Object o : nodes) {
      final Element c = (Element) o;
      result.add(c.attributeValue("name"));
    }

    return result;
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
