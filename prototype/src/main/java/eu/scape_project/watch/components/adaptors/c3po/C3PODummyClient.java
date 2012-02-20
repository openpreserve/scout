package eu.scape_project.watch.components.adaptors.c3po;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class C3PODummyClient implements IC3POClient {

  private static final String JOB_UUID = "550e8400-e29b-41d4-a716-446655440000";

  @Override
  public List<String> getCollectionIdentifiers() {
    return Collections.unmodifiableList(Arrays.asList("Test"));
  }

  @Override
  public String submitCollectionProfileJob(String identifier, List<String> properties) {
    return JOB_UUID;
  }

  @Override
  public InputStream pollJobResult(String uuid) {
    InputStream is = null;
    if (uuid.equals(JOB_UUID)) {
      is = C3PODummyClient.class.getClassLoader().getResourceAsStream("profiles/dummy_profile.xml");
    }

    return is;
  }

}
