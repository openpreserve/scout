package eu.scape_project.watch.core.model;

import java.util.List;
import java.util.UUID;

public class AsyncRequest {
  
  private String id;
  
  private List<Trigger> triggers;
  
  public AsyncRequest() {
    super();
    this.setId(UUID.randomUUID().toString());
  }
  
  public AsyncRequest(String id) {
    this.setId(id);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Trigger> getTriggers() {
    return triggers;
  }

  public void setTriggers(List<Trigger> triggers) {
    this.triggers = triggers;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AsyncRequest other = (AsyncRequest) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (triggers == null) {
      if (other.triggers != null)
        return false;
    } else if (!triggers.equals(other.triggers))
      return false;
    return true;
  }
}
