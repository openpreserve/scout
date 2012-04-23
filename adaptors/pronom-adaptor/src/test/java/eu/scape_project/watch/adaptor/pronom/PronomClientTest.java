package eu.scape_project.watch.adaptor.pronom;

import eu.scape_project.watch.adaptor.pronom.client.PronomClient;
import eu.scape_project.watch.adaptor.pronom.client.PronomServiceCommunicator;
import eu.scape_project.watch.adaptor.pronom.common.OutputFormat;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PronomClientTest {

  @Test
  public void shouldSubmitQuery() throws Exception {
    String sparql = "SELECT * WHERE {GRAPH ?g { ?s ?p ?o . }}";
    String greet = "Hello, Pronom!";

    PronomServiceCommunicator comm = mock(PronomServiceCommunicator.class);

    when(comm.submitQuery(Mockito.anyString(), Mockito.anyString())).thenReturn(greet);

    PronomClient service = new PronomClient("http://test.linkeddatapronom.nationalarchives.gov.uk", comm);
    String result = service.query(sparql, OutputFormat.JSON);

    Mockito.verify(comm).submitQuery(sparql + "LIMIT 10 OFFSET 0", "json");
    Assert.assertNotNull(result);
    Assert.assertEquals(result, greet);
  }
  
}
