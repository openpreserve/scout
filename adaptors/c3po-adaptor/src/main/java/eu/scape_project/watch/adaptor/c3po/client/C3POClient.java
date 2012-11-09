package eu.scape_project.watch.adaptor.c3po.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.adaptor.c3po.common.C3POResponseParser;
import eu.scape_project.watch.adaptor.c3po.common.ProductionProfileStrategy;
import eu.scape_project.watch.adaptor.c3po.common.ProfileVersionReader;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * The c3po client connects to a c3po instance and fetches a profile for the
 * specified collection.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class C3POClient implements C3POClientInterface {

  /**
   * A default logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(C3POClient.class);

  /**
   * A short timeout interval for the requests. Amounts to 30 seconds.
   */
  private static final int SHORT_TIMEOUT_INTERVAL = 1000 * 30;

  /*
   * This 10 minute read timeout is used for the getProfile requests. It works
   * for now as the collections are of reasonable size and 10 minutes are
   * sufficient to generate the profile (if this is the first generation
   * request). Even if it occurs, the profile still gets generated and cached on
   * the server, so it will be obtained on the next request. A better solution
   * is of course to support polling on the server side and ask for the status
   * of the generation as initially planned.
   */
  /**
   * A longer timeout interval for the requests. Amounts to 10 minutes.
   */
  private static final int LONG_TIMEOUT_INTERVAL = 1000 * 60 * 10;

  /**
   * The endpoint of the c3po service.
   */
  private String apiEndpoint;

  /**
   * The port of the service.
   */
  private int port;

  /**
   * Creates a client and sets the endpoint.
   * 
   * @param url
   *          the endpoint to use in the client.
   */
  public C3POClient(final String url) {
    this.apiEndpoint = url;
  }

  /**
   * Creates a client and sets the endpoint to the specified url with the
   * specified port in the following form: 'url:port'.
   * 
   * @param url
   *          the url of the endpoint.
   * @param port
   *          the port where the service is running.
   */
  public C3POClient(final String url, final int port, final String endpoint) {

    this.apiEndpoint = url;

    if (port >= 0 && port <= 65535) {
      this.port = port;
      this.apiEndpoint = this.apiEndpoint + ":" + port;
    }

    if (endpoint != null) {
      if (endpoint.startsWith("/")) {
        this.apiEndpoint = this.apiEndpoint + endpoint;
      } else {
        this.apiEndpoint = this.apiEndpoint + "/" + endpoint;
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCollectionIdentifiers() throws PluginException {
    final List<String> result = new ArrayList<String>();

    try {
      final String response = this.submitRequest("/collections");
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
      final String response = this.submitRequest("/export/profile?collection=" + identifier, LONG_TIMEOUT_INTERVAL);

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

  @Override
  public ProfileVersionReader getReader() {
    return new ProductionProfileStrategy();
  }

  /**
   * This method is deprecated and no longer supported.
   * 
   * @return throws exception.
   */
  @Override
  @Deprecated
  public String submitCollectionProfileJob(final String identifier, final List<String> properties) {
    throw new UnsupportedOperationException("This method is deprecated and not supported anymore");
  }

  /**
   * This method is deprecated and no longer supported.
   * 
   * @return throws exception.
   */
  @Override
  @Deprecated
  public InputStream pollJobResult(final String uuid) {
    throw new UnsupportedOperationException("This method is deprecated and not supported anymore");
  }

  public String getApiEndpoint() {
    return apiEndpoint;
  }

  public void setApiEndpoint(final String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
  }

  public int getPort() {
    return port;
  }

  public void setPort(final int port) {
    this.port = port;
  }

  /**
   * Submits a c3po request with a short read timeout.
   * 
   * @param request
   *          the request to submit.
   * @return the response as a string.
   * @throws IOException
   *           if an error occurs.
   */
  private String submitRequest(final String request) throws IOException {
    return this.submitRequest(request, SHORT_TIMEOUT_INTERVAL);
  }

  /**
   * Submits a c3po request with the given read timeout.
   * 
   * @param request
   *          the request to submit.
   * @return the response as a string.
   * @throws IOException
   *           if an error occurs.
   */
  private String submitRequest(final String request, final int readTimeOut) throws IOException {
    LOG.debug("Submitting request to c3po: {}", request);

    final URL location = new URL(this.apiEndpoint + request);
    final HttpURLConnection connection = (HttpURLConnection) location.openConnection();

    connection.setDoInput(true);
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/xml");
    connection.setConnectTimeout(SHORT_TIMEOUT_INTERVAL);
    connection.setReadTimeout(readTimeOut);

    return this.readResponse(connection);
  }

  /**
   * Reads the response from the {@link URLConnection}.
   * 
   * @param connection
   *          the connection through which the query was made.
   * @return the response as a String.
   */
  private String readResponse(final URLConnection connection) {
    final StringBuffer response = new StringBuffer("");
    BufferedReader in = null;
    String result = "";
    try {
      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String str;

      while (null != ((str = in.readLine()))) {
        response.append(str + System.getProperty("line.separator"));
      }

      result = response.toString();
      LOG.debug("Received response from c3po: {}", result.substring(0, result.indexOf("\n")) + "... ");
      LOG.trace(result);

    } catch (final IOException e) {
      LOG.error("An error occurred while reading the response", e);

    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (final IOException e) {
        LOG.error("An error occurred while closing the input stream");
      }
    }

    return result;

  }
}
