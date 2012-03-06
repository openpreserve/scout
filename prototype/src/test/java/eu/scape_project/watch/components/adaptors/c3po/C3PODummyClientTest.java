package eu.scape_project.watch.components.adaptors.c3po;

import static eu.scape_project.watch.components.adaptors.c3po.C3PODummyClient.COLLECTION_NAME;

import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the c3po dummy client.
 * 
 * @author peter
 * 
 */
public class C3PODummyClientTest {

  /**
   * The object under test.
   */
  private C3PODummyClient client;

  /**
   * Inits the test object.
   */
  @Before
  public void setup() {
    this.client = new C3PODummyClient();
  }

  /**
   * Tests the retrieval of collection identifiers.
   */
  @Test
  public void shouldRetrieveTheAvaliableCollectionIdentifiers() {
    final List<String> identifiers = this.client.getCollectionIdentifiers();
    Assert.assertEquals(1, identifiers.size());
    Assert.assertEquals(COLLECTION_NAME, identifiers.get(0));
  }

  /**
   * Tests the job submission of the dummy client.
   */
  @Test
  public void shouldSubmitASuccessfulJob() {
    final String job = this.client.submitCollectionProfileJob(COLLECTION_NAME, null);
    Assert.assertNotNull(job);
    Assert.assertNotSame("", job);
  }

  /**
   * Tests the submission of a false identifier.
   */
  @Test
  public void shouldSubmitAnUnsuccessfulJob() {
    final String job = this.client.submitCollectionProfileJob("missing", null);
    Assert.assertNull(job);
  }

  /**
   * Opens up an input stream to a content profile.
   */
  @Test
  public void shouldObtainCollectionProfile() {
    InputStream result = this.client.pollJobResult("false");
    Assert.assertNull(result);

    final List<String> identifiers = this.client.getCollectionIdentifiers();
    final String collection = identifiers.get(0);
    final String job = this.client.submitCollectionProfileJob(collection, null);
    result = this.client.pollJobResult(job);

    Assert.assertNotNull(result);

  }
}
