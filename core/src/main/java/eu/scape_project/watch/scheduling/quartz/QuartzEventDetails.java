package eu.scape_project.watch.scheduling.quartz;

import eu.scape_project.watch.interfaces.EventDetails;

public class QuartzEventDetails implements EventDetails {
  
  
  private boolean successful;
  
  private String message;

  private String reason;
  
  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public void setSuccessful(boolean suc) {
    this.successful = suc;   
  }

  @Override
  public boolean isSucessful() {
    return successful;
  }

  @Override
  public void addMessage(String msg) {
    message = msg;
  }

  @Override
  public void setReason(String reason) {
    this.reason = reason;
    
  }

  @Override
  public String getReason() {
    return reason;
  } 

}
