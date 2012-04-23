package eu.scape_project.watch.adaptor.pronom;

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

public class PRONOMService {

  private URL baseURL;
  private URL sparqlURL;

  public PRONOMService(final String u) throws MalformedURLException {
    this.baseURL = new URL(u);
    this.sparqlURL = new URL(this.baseURL + "/sparql/endpoint.php");
  }

  public String query(final String sparql) throws ProtocolException, IOException {
    return query(sparql, OutputFormat.JSON);
  }

  public String query(String sparql, OutputFormat format) throws ProtocolException, IOException {
    return query(sparql, format, 10, 0);
  }

  public String query(final String s, final OutputFormat fmt, final int l, final int o) throws ProtocolException,
    IOException {

    final String sparql = s + this.getLimit(l) + " " + this.getOffset(o);
    final String format = fmt.name().toLowerCase();
    final URLConnection connection = this.getConnection();

    this.submitQuery(connection, sparql, format);

    return this.readResponse(connection);
  }

  public URLConnection getConnection() throws ProtocolException, IOException {
    final HttpURLConnection connection = (HttpURLConnection) sparqlURL.openConnection();

    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    return connection;
  }

  private void submitQuery(final URLConnection conn, final String spql, final String fmt) throws IOException {
    final DataOutputStream ps = new DataOutputStream(conn.getOutputStream());

    ps.writeBytes("&query=" + URLEncoder.encode(spql, "UTF-8") + "&output=" + fmt);
    ps.flush();
    ps.close();
  }

  private String readResponse(URLConnection connection) throws ProtocolException, IOException {
    final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    final StringBuffer response = new StringBuffer();
    String str;

    while (null != ((str = in.readLine()))) {
      response.append(str + System.getProperty("line.separator"));
    }

    in.close();

    return response.toString();
  }

  private String getLimit(final int l) {
    int limit = 10;

    if (l > 0 && l <= 1000) {
      limit = l;
    }

    return "LIMIT " + limit;
  }

  private String getOffset(final int o) {
    int offset = 0;

    if (o >= 0) {
      offset = o;
    }

    return "OFFSET " + offset;
  }
}
