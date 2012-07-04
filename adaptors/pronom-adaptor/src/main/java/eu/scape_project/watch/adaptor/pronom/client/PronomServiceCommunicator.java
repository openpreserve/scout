package eu.scape_project.watch.adaptor.pronom.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import eu.scape_project.watch.adaptor.pronom.common.CommunicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The pronom service communicator submits queries to a pronom web service
 * endpoint. The query is url encoded and passed as the 'query' parameter and
 * the output format is passed as the 'output' parameter.
 * 
 * The service waits for the response and returns it.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PronomServiceCommunicator {

  private static final Logger LOG = LoggerFactory.getLogger(PronomServiceCommunicator.class);

  /**
   * The default time out set to 15 seconds.
   */
  private static final int TIMEOUT_INTERVAL = 15 * 1000;

  /**
   * The endpoint.
   */
  public URL url;

  /**
   * Creates a communicator for the specified url.
   * 
   * @param u
   * @throws MalformedURLException
   */
  public PronomServiceCommunicator(final String u) throws MalformedURLException {
    this.url = new URL(u);
  }

  /**
   * Submits a sparql query according to the specification of the endpoint.
   * 
   * @param spql
   *          the query.
   * @param fmt
   *          the output format.
   * @return the result.
   */
  public String submitQuery(final String spql, final String fmt) throws CommunicationException {
    DataOutputStream ps = null;
    try {
      final HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();

      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setConnectTimeout(TIMEOUT_INTERVAL);
      connection.setReadTimeout(TIMEOUT_INTERVAL);

      ps = new DataOutputStream(connection.getOutputStream());
      ps.writeBytes("&query=" + URLEncoder.encode(spql, "UTF-8") + "&output=" + fmt);
      ps.flush();

      return this.readResponse(connection.getInputStream());

    } catch (final IOException e) {
      LOG.error("An error occurred while submitting the query '{}': {}", spql, e.getMessage());
      throw new CommunicationException("An error occurred while talking with the PRONOM Service at '"
        + this.url.toString() + "': " + e.getMessage(), e);
    } finally {
      try {
        if (ps != null) {
          ps.close();
        }
      } catch (final IOException e) {
        LOG.error("An error occurred while closing the output stream");
      }
    }
  }

  /**
   * Reads the response from the response input stream.
   * 
   * @param stream
   *          the input stream to read from.
   * @return the response as a String.
   */
  private String readResponse(final InputStream stream) {
    final StringBuffer response = new StringBuffer("");
    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(stream));
      String str;

      while (null != ((str = in.readLine()))) {
        response.append(str + System.getProperty("line.separator"));
      }

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

    return response.toString();
  }
}
