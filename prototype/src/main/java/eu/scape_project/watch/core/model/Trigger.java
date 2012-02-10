package eu.scape_project.watch.core.model;

import java.util.List;

public class Trigger {
  
  private AsyncRequest request;

  private Plan plan;
  
  private Question question;
  
  private List<Notification> notification;
  
  public Trigger() {
    super();
  }
  
  public Trigger(AsyncRequest req, List<Notification> n, Plan p) {
    this.request = req;
    this.notification = n;
    this.plan = p;
  }

  public AsyncRequest getRequest() {
    return request;
  }

  public void setRequest(AsyncRequest request) {
    this.request = request;
  }

  public List<Notification> getNotification() {
    return notification;
  }

  public void setNotification(List<Notification> notification) {
    this.notification = notification;
  }

  public Plan getPlan() {
    return plan;
  }

  public void setPlan(Plan plan) {
    this.plan = plan;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((notification == null) ? 0 : notification.hashCode());
    result = prime * result + ((plan == null) ? 0 : plan.hashCode());
    result = prime * result + ((request == null) ? 0 : request.hashCode());
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
    Trigger other = (Trigger) obj;
    if (notification == null) {
      if (other.notification != null)
        return false;
    } else if (!notification.equals(other.notification))
      return false;
    if (plan == null) {
      if (other.plan != null)
        return false;
    } else if (!plan.equals(other.plan))
      return false;
    if (request == null) {
      if (other.request != null)
        return false;
    } else if (!request.equals(other.request))
      return false;
    return true;
  }

  public Question getQuestion() {
    return question;
  }

	public void setQuestion(Question question) {
    this.question = question;
  }

}
