package eu.scape_project.watch.monitor;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.notification.NotificationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CentralMonitor implements DOListener<AsyncRequest> {

  private static final Logger LOG = LoggerFactory.getLogger(CentralMonitor.class);

  /**
   * List of all Monitors
   */
  private List<MonitorInterface> monitors;

  /**
   * List of all AsyncRequests
   */
  private List<AsyncRequest> aRequests;

  /**
   * Notification Service 
   */
  private NotificationService nService;

  /**
   * Default Constructor 
   */
  public CentralMonitor() {
    monitors = new ArrayList<MonitorInterface>();
    aRequests = new ArrayList<AsyncRequest>();
    LOG.info("CentralMonitor initialized");
  }

  /**
   * Adding Monitor to a CentralMonitor 
   * @param monitor
   */
  public void addMonitor(MonitorInterface monitor) {
    monitors.add(monitor);
    monitor.registerCentralMonitor(this);
  }

  /**
   * Register CentralMonitor to listen when an AsyncRequest is 
   * created/updated/deleted
   */
  public void registerToAsyncRequest() {
    DAO.addDOListener(AsyncRequest.class, this);
    LOG.debug("CentralMonitor listening AsyncRequest");
  }

  public void setNotificationService(NotificationService ns) {
    nService = ns;
  }

  public NotificationService getNotificationService() {
    return nService;
  }

  /**
   * Give a list of AsyncRequests ids to be evaluated.
   * @param ids - list of AsyncRequest ids to be notified
   */
  public void notifyAsyncRequests(List<String> ids) {
    for (String uuid : ids) {
      AsyncRequest tmp = findAsyncRequest(uuid);
      assessRequest(tmp);
    }
  }

  public List<AsyncRequest> getAllRequests() {
    return aRequests;
  }

  public List<MonitorInterface> getAllMonitors() {
    return monitors;
  }

  
  @Override
  public void onUpdated(AsyncRequest req) {
    LOG.debug("adding Request to monitors " + req.getId());
    if (!aRequests.contains(req)) {
      aRequests.add(req);
      for (MonitorInterface monitor : monitors) {
        monitor.addWatchRequest(req);
      }
    }
  }

  @Override
  public void onRemoved(AsyncRequest object) {
    aRequests.remove(object);
    // TODO notify monitors about removal
  }

  private AsyncRequest findAsyncRequest(String uuid) {
    for (AsyncRequest i : aRequests) {
      if (i.getId().equals(uuid)){
        return i;
      }
    }
    return null;
  }

  private void assessRequest(AsyncRequest aRequest) {
    LOG.info("Assessing AsyncRequest " + aRequest.getId());
    for (Trigger trigger : aRequest.getTriggers()) {
      Question question = trigger.getQuestion();
      List<PropertyValue> result = DAO.PROPERTY_VALUE.query(question.getSparql(), 0, 10);
      if (result.size() > 0) {
        notify(trigger);
      } else {
        LOG.info("Condition is not satisfied");
      }
    }
  }

  private void notify(Trigger trigger) {
    if (nService != null) {
      for (Notification notification : trigger.getNotifications()) {
        nService.send(notification);
      }
    } else {
      LOG.warn("No NotificationService specified");
    }
  }
}
