package eu.scape_project.watch.adaptor.pronom;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import eu.scape_project.watch.adaptor.pronom.client.PronomClient;
import eu.scape_project.watch.adaptor.pronom.client.PronomServiceCommunicator;
import eu.scape_project.watch.adaptor.pronom.common.OutputFormat;

/**
 * Tests the pronom client.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PronomClientTest {

  /**
   * Mocks the connection and submits a query.
   * 
   * @throws Exception
   */
  @Test
  public void shouldSubmitQuery() throws Exception {
    String sparql = "SELECT * WHERE {GRAPH ?g { ?s ?p ?o . }}";
    String greet = "Hello, Pronom!";

    PronomServiceCommunicator comm = mock(PronomServiceCommunicator.class);

    when(comm.submitQuery(Mockito.anyString(), Mockito.anyString())).thenReturn(greet);

    PronomClient service = new PronomClient(comm);
    String result = service.query(sparql, OutputFormat.JSON);

    Mockito.verify(comm).submitQuery(sparql + "LIMIT 10 OFFSET 0", "json");
    Assert.assertNotNull(result);
    Assert.assertEquals(result, greet);
  }

  /*
   * Needs Internet connection and relies on the pronom service. Do we want this
   * test?
   */
  /**
   * Submits a real query to the service.
   * 
   * @throws Exception
   *           if something goes wrong.
   */
  @Ignore @Test
  public void shouldSubmitRealQuery() throws Exception {
    final PronomClient service = new PronomClient(new PronomServiceCommunicator(PronomAdaptor.ENDPOINT));
    final String q = IOUtils.toString(new FileInputStream("src/main/resources/query.txt"));
    final String query = q.replace(System.getProperty("line.separator"), "");

    final String response = service.query(query, OutputFormat.JSON, 10, 10);
    Assert.assertNotNull(response);
  }

}
