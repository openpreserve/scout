package eu.scape_project.watch.adaptor.pronom.client;

import java.net.MalformedURLException;

import eu.scape_project.watch.adaptor.pronom.common.CommunicationException;
import eu.scape_project.watch.adaptor.pronom.common.OutputFormat;

/**
 * The pronom client provides facilities for querying a PRONOM endpoint web
 * service.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PronomClient {

  /**
   * The communicator connectiong handling.
   */
  private PronomServiceCommunicator communicator;

  /**
   * Creates a client.
   * 
   * @param comm
   *          the communicator to use.
   * @throws MalformedURLException
   *           if the ulr of the endpoint is malformed.
   */
  public PronomClient(final PronomServiceCommunicator comm) throws MalformedURLException {
    this.communicator = comm;
  }

  /**
   * Queries the endpoint and assumes some default values. The output format is
   * {@link OutputFormat#JSON}, the default batch is 10 and the offset is 0.
   * 
   * @param sparql
   *          the query to submit.
   * @return the result of the query.
   * 
   * @throws CommunicationException
   *           if the endpoint throws an exception.
   */
  public String query(final String sparql) throws CommunicationException {
    return query(sparql, OutputFormat.JSON);
  }

  /**
   * The same as {@link PronomClient#query(String)} but allows you to specify
   * the {@link OutputFormat}.
   * 
   * @param sparql
   *          the query to submit
   * @param format
   *          the response format.
   * @return the result of the query.
   * 
   * @throws CommunicationException
   *           if the endpoint throws an exception.
   */
  public String query(String sparql, OutputFormat format) throws CommunicationException {
    return query(sparql, format, 10, 0);
  }

  /**
   * Submits a query to the PRONOM endpoint with the following charateristics.
   * 
   * @param s
   *          the query.
   * @param fmt
   *          the output format.
   * @param l
   *          the batch size.
   * @param o
   *          the offset.
   * @return the result of the query.
   * 
   * @throws CommunicationException
   *           if the endpoint throws an exception.
   */
  public String query(final String s, final OutputFormat fmt, final int l, final int o) throws CommunicationException {
    final String format = fmt.name().toLowerCase();
    final String query = this.buildQuery(s, l, o);

    return this.communicator.submitQuery(query, format);
  }

  /**
   * Constructs the query for submission.
   * 
   * @param s
   *          the main part
   * @param l
   *          the limit
   * @param o
   *          the offset
   * @return the query.
   */
  private String buildQuery(final String s, final int l, final int o) {
    return s + this.getLimit(l) + " " + this.getOffset(o);
  }

  /**
   * Gets the limit and escapes bad input. The default of 10 is used if the
   * input is incorrect.
   * 
   * @param l
   *          the limit
   * @return the limit part of the query.
   */
  private String getLimit(final int l) {
    int limit = 10;

    if (l > 0 && l <= 1000) {
      limit = l;
    }

    return "LIMIT " + limit;
  }

  /**
   * Gets the offset and escapes bad input. The default of 0 is used if the
   * input is incorrect.
   * 
   * @param o
   *          the offset
   * @return the offset part of the query.
   */
  private String getOffset(final int o) {
    int offset = 0;

    if (o >= 0) {
      offset = o;
    }

    return "OFFSET " + offset;
  }
}
