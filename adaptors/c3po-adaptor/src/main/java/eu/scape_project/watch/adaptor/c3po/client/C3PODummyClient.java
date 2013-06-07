package eu.scape_project.watch.adaptor.c3po.client;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.scape_project.watch.adaptor.c3po.common.DummyReader;
import eu.scape_project.watch.adaptor.c3po.common.ProfileVersionReader;

/**
 * A dummy client that always returns the same job uuid and returns a random
 * collection profile for its only collection named 'coll-0-test'.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class C3PODummyClient implements C3POClientInterface {

  /**
   * A simple string format for the dummy profile files.
   */
  private static final String DUMMY_FORMAT = "profiles/dummy_profile_%d.xml";

  /**
   * A hardcoded job uuid.
   */
  private static final String JOB_UUID = "550e8400-e29b-41d4-a716-446655440000";

  /**
   * A hardcoded collection name.
   */
  public static final String COLLECTION_NAME = "coll-0-test";

  @Override
  public List<String> getCollectionIdentifiers() {
    return Collections.unmodifiableList(Arrays.asList(COLLECTION_NAME));
  }

  @Override
  public String submitCollectionProfileJob(final String identifier, final List<String> properties) {
    String job = null;
    if (identifier.equals(COLLECTION_NAME)) {
      job = JOB_UUID;
    }
    return job;
  }

  @Override
  public InputStream pollJobResult(final String uuid) {
    InputStream is = null;
    if (uuid.equals(JOB_UUID)) {
      final Random r = new Random();
      final int suffix = r.nextInt(42) % 4; // ooo magic...
      // final int suffix = 3;
      is = C3PODummyClient.class.getClassLoader().getResourceAsStream(String.format(DUMMY_FORMAT, suffix));
    }

    return is;
  }

  @Override
  public InputStream getCollectionProfile(String identifier, Map<String, String> parameters) {
    return this.pollJobResult(this.submitCollectionProfileJob(identifier, null));
  }

  @Override
  public ProfileVersionReader getReader() {
    return new DummyReader();
  }

}
