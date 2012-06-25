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

public class PronomServiceCommunicator {

  public URL url;

  public PronomServiceCommunicator(final String u) throws MalformedURLException {
    this.url = new URL(u);
  }

  public String submitQuery(final String spql, final String fmt) throws ProtocolException, IOException {
    final HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();

    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    final DataOutputStream ps = new DataOutputStream(connection.getOutputStream());

    ps.writeBytes("&query=" + URLEncoder.encode(spql, "UTF-8") + "&output=" + fmt);
    ps.flush();
    ps.close();

    return this.readResponse(connection);
  }

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
