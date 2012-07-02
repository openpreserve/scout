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

public class C3POClient implements C3POClientInterface {

  private static final Logger LOG = LoggerFactory.getLogger(C3POClient.class);

  private static final int TIMEOUT_INTERVAL = 1000 * 60;

  private String apiEndpoint;

  private int port;

  public C3POClient(String url) {
    this.setApiEndpoint(url);
  }

  public C3POClient(String url, int port) {
    this(url + ":" + port);
    this.setPort(port);
  }

  public List<String> getCollectionIdentifiers() {
    final List<String> result = new ArrayList<String>();

    try {
      final String response = this.submitRequest("/collections");
      final C3POResponseParser reader = new C3POResponseParser();
      result.addAll(reader.getCollectionsFromResponse(IOUtils.toInputStream(response)));
    } catch (ProtocolException e) {
      e.printStackTrace();
      LOG.error("A protocol error occurred: {}", e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("An io error occurred: {}", e.getMessage());
    }

    return result;
  }

  @Override
  public InputStream getCollectionProfile(String identifier, Map<String, String> parameters) {
    try {
      final String response = this.submitRequest("/export/profile?collection=" + identifier);
      if (response != null) {

        return IOUtils.toInputStream(response);
      }
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public ProfileVersionReader getReader() {
    return new ProductionProfileStrategy();
  }

  public String submitCollectionProfileJob(String identifier, List<String> properties) {
    throw new UnsupportedOperationException("This method is deprecated and not supported anymore");
  }

  public InputStream pollJobResult(String uuid) {
    throw new UnsupportedOperationException("This method is deprecated and not supported anymore");
  }

  public String getApiEndpoint() {
    return apiEndpoint;
  }

  public void setApiEndpoint(String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  private String submitRequest(final String request) throws ProtocolException, IOException {
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
   * @throws ProtocolException
   *           if something goes wrong
   * @throws IOException
   *           if something goes wrong
   */
  private String readResponse(final URLConnection connection) throws ProtocolException, IOException {
    final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    final StringBuffer response = new StringBuffer();
    String str;

    while (null != ((str = in.readLine()))) {
      response.append(str + System.getProperty("line.separator"));
    }

    in.close();

    final String result = response.toString();
    LOG.debug("Received response from c3po: {}", result);
    return result;
  }
}
