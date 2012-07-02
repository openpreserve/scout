package eu.scape_project.watch.adaptor.c3po;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.watch.adaptor.c3po.client.C3POClient;

public class C3POClientTest {

  /*
   * Assumes you have c3po running locally on port 8080
   */
  @Ignore
  @Test
  public void shouldTestRealRunningClient() {
    final C3POClient client = new C3POClient("http://127.0.0.1", 8080);
    final List<String> list = client.getCollectionIdentifiers();
    Assert.assertFalse(list.isEmpty());

    final String id = list.get(0);
    final InputStream stream = client.getCollectionProfile(id, null);
    Assert.assertNotNull(stream);
    try {
      String string = IOUtils.toString(stream);
      Assert.assertNotNull(string);
      System.out.println(string);
    } catch (IOException e) {
      Assert.fail();
    }
  }
}
