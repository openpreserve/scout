package eu.scape_project.watch.adaptor.c3po;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class C3POClient implements IC3POClient {

  private String apiEndpoint;

  private int port;

  public C3POClient(String url) {
    this.setApiEndpoint(url);
  }

  public C3POClient(String url, int port) {
    this(url + ":" + port);
    this.setPort(port);
  }

  public List<String> getCollectionIdentifiers() {
    return new ArrayList<String>();
  }

  public String submitCollectionProfileJob(String identifier, List<String> properties) {
    return null;
  }
  
  public InputStream pollJobResult(String uuid) {
    return null;
  }

  public String getApiEndpoint() {
    return apiEndpoint;
  }

  public void setApiEndpoint(String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

}
