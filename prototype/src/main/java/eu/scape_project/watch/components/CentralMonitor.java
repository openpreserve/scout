package eu.scape_project.watch.components;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.components.interfaces.IMonitor;
import eu.scape_project.watch.core.model.AsyncRequest;
import eu.scape_project.watch.core.model.Question;

public class CentralMonitor extends Thread {

  private List<IMonitor> monitors;

  public CentralMonitor() {
    monitors = new ArrayList<IMonitor>();
    // System.out.println("Central service started");
  }

  public void registerMonitor(IMonitor monitor) {
    monitor.registerCentralMonitor(this);
    monitors.add(monitor);
  }

  public void addWatchRequest(AsyncRequest wr) {
    List<Question> tmp = wr.getQuestion();
    for (Question q : tmp) {
      addWatchQuestion(q, wr);
    }
  }

  public synchronized void notifyWatchRequests(List<String> wrIds) {
    System.out.println("Notifying " + wrIds);
  }

  // this function is wrong fix it by adding monitor pool
  public void run() {
    for (int i = 0; i < monitors.size(); i++) {
      Thread t = monitors.get(i).startMonitoring();
      try {
        t.join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private void addWatchQuestion(Question q, AsyncRequest wr) {
    for (int i = 0; i < monitors.size(); i++) {
      if (monitors.get(i).checkForEntityType(q.getEntityType())) {
        // System.out.println("Adding watch question");
        monitors.get(i).addQuestion(q, wr.getId(), q.getPeriod());
        return; // only one monitor will answer question
      }
    }
  }

}
