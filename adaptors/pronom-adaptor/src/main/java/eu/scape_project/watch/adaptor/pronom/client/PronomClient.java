package eu.scape_project.watch.adaptor.pronom.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import eu.scape_project.watch.adaptor.pronom.common.OutputFormat;

public class PronomClient {

  private PronomServiceCommunicator communicator;

  public PronomClient(final PronomServiceCommunicator comm) throws MalformedURLException {
    this.communicator = comm;
  }

  public String query(final String sparql) throws ProtocolException, IOException {
    return query(sparql, OutputFormat.JSON);
  }

  public String query(String sparql, OutputFormat format) throws ProtocolException, IOException {
    return query(sparql, format, 10, 0);
  }

  public String query(final String s, final OutputFormat fmt, final int l, final int o) throws ProtocolException,
    IOException {
    final String format = fmt.name().toLowerCase();
    final String query = this.buildQuery(s, l, o);

    return this.communicator.submitQuery(query, format);
  }

  private String buildQuery(final String s, final int l, final int o) {
    return s + this.getLimit(l) + " " + this.getOffset(o);
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
