package eu.scape_project.watch.adaptor.pronom.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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
   * @throws ProtocolException
   *           if something goes wrong.
   * @throws IOException
   *           if something goes wrong.
   */
  public String submitQuery(final String spql, final String fmt) throws ProtocolException, IOException {
    final HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();

    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    connection.setConnectTimeout(TIMEOUT_INTERVAL);
    connection.setReadTimeout(TIMEOUT_INTERVAL);

    final DataOutputStream ps = new DataOutputStream(connection.getOutputStream());

    ps.writeBytes("&query=" + URLEncoder.encode(spql, "UTF-8") + "&output=" + fmt);
    ps.flush();
    ps.close();

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

    return response.toString();
  }
}
