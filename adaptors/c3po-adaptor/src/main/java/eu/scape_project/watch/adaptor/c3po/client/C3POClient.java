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

import eu.scape_project.watch.adaptor.c3po.common.C3POResponseParser;
import eu.scape_project.watch.adaptor.c3po.common.ProductionProfileStrategy;
import eu.scape_project.watch.adaptor.c3po.common.ProfileVersionReader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   * A default timeout interval for the service.
   */
  private static final int TIMEOUT_INTERVAL = 1000 * 60;

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
    this.setApiEndpoint(url);
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
  public C3POClient(final String url, final int port) {
    this(url + ":" + port);
    this.setPort(port);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCollectionIdentifiers() {
    final List<String> result = new ArrayList<String>();

    try {
      final String response = this.submitRequest("/collections");
      final C3POResponseParser reader = new C3POResponseParser();
      result.addAll(reader.getCollectionsFromResponse(IOUtils.toInputStream(response)));
    } catch (final ProtocolException e) {
      e.printStackTrace();
      LOG.error("A protocol error occurred: {}", e.getMessage());
    } catch (final IOException e) {
      e.printStackTrace();
      LOG.error("An io error occurred: {}", e.getMessage());
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getCollectionProfile(final String identifier, final Map<String, String> parameters) {
    try {
      final String response = this.submitRequest("/export/profile?collection=" + identifier);
      if (response != null) {

        return IOUtils.toInputStream(response);
      }
    } catch (final ProtocolException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return null;
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
   * Submits a c3po request.
   * 
   * @param request
   *          the request to submit.
   * @return the response as a string.
   * @throws IOException
   *           if an error occurrs.
   */
  private String submitRequest(final String request) throws IOException {
    LOG.debug("Submitting request to c3po: {}", request);

    final URL location = new URL(this.apiEndpoint + request);
    final HttpURLConnection connection = (HttpURLConnection) location.openConnection();

    connection.setDoInput(true);
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/xml");
    connection.setConnectTimeout(TIMEOUT_INTERVAL);
    connection.setReadTimeout(TIMEOUT_INTERVAL);

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
      LOG.debug("Received response from c3po: {}", result);

    } catch (final IOException e) {
      LOG.error("An error occurred while reading the response");
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
