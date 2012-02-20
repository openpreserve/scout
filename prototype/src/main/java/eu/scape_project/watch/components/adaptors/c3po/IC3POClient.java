package eu.scape_project.watch.components.adaptors.c3po;

import java.io.InputStream;
import java.util.List;

public interface IC3POClient {

  List<String> getCollectionIdentifiers();
  
  String submitCollectionProfileJob(String identifier, List<String> properties);
  
  InputStream pollJobResult(String uuid);
}
