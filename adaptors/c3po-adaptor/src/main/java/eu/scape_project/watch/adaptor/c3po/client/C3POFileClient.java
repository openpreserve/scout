package eu.scape_project.watch.adaptor.c3po.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.adaptor.c3po.common.C3POResponseParser;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * C3PO client interface which can connect to a defined folder on the web and
 * download collection profiles. That folder is expected to contain
 * collections.xml file which contains a list of collections. It should also
 * contain collection profiles (xml files) for each collection defined in
 * collections.xml.
 * 
 * @author Kresimir Duretec <duretec@ifs.tuwien.ac.at>
 * 
 */
public class C3POFileClient extends C3POClient {

  /**
   * A default logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(C3POFileClient.class);

  /**
   * Creates a client and sets the endpoint.
   * 
   * @param url
   *          the endpoint to use in the client.
   */
  public C3POFileClient(final String url) {
    this.apiEndpoint = url;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCollectionIdentifiers() throws PluginException {
    final List<String> result = new ArrayList<String>();

    try {
      final String response = this.submitRequest("/collections.xml");

      LOG.trace("Collections xml: " + response);

      final C3POResponseParser reader = new C3POResponseParser();
      final List<String> collections = reader.getCollectionsFromResponse(IOUtils.toInputStream(response));

      if (collections == null) {
        throw new PluginException("Could not read response: " + response);
      }

      result.addAll(collections);
    } catch (final ProtocolException e) {
      LOG.error("A protocol error occurred: {}", e.getMessage());
      throw new PluginException("A protocol error occurred (check the url)", e);

    } catch (final IOException e) {
      LOG.error("An io error occurred: {}", e.getMessage());
      throw new PluginException("A io exception occurred (check the connection)", e);
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getCollectionProfile(final String identifier, final Map<String, String> parameters)
    throws PluginException {
    try {
      final String response = this.submitRequest("/" + identifier + ".xml", LONG_TIMEOUT_INTERVAL);

      if (response == null || response.equals("")) {
        throw new PluginException("Bad response from server [" + this.apiEndpoint + "]. Check the logs and retry");
      } else {
        return IOUtils.toInputStream(response);
      }

    } catch (final ProtocolException e) {
      LOG.error("A protocol error occurred: {}", e.getMessage());
      throw new PluginException("A protocol error occurred (check the url)", e);

    } catch (final IOException e) {
      LOG.error("An io error occurred: {}", e.getMessage());
      throw new PluginException("A io exception occurred (check the connection)", e);
    }
  }
}
