package eu.scape_project.watch.monitor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.dao.AsyncRequestDAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.interfaces.MonitorInterface;

public class CentralMonitor implements DOListener {

  private static final Logger LOG = LoggerFactory.getLogger(CentralMonitor.class);

  private List<MonitorInterface> monitors;

  private List<AsyncRequest> aRequests;

  private AsyncRequestDAO asyncRequestDAO;

  public CentralMonitor() {
    monitors = new ArrayList<MonitorInterface>();
    aRequests = new ArrayList<AsyncRequest>();
    LOG.info("CentralMonitor initialized");
  }

  public void addMonitor(MonitorInterface monitor) {
    monitors.add(monitor);
    monitor.registerCentralMonitor(this);
  }

  public void registerToAsyncRequest(AsyncRequestDAO ar) {
    asyncRequestDAO = ar;
    asyncRequestDAO.addDOListener(this);
    LOG.info("CentralMonitor listening AsyncRequestDAO");
  }

  public void notifyAsyncRequests(List<String> ids) {
    
    for (String uuid : ids){
      AsyncRequest tmp = findAsyncRequest(uuid);
      assessRequest(tmp);
    }
  }

  @Override
  public void onUpdated(RdfBean object) {
    AsyncRequest req = (AsyncRequest) object;
    LOG.info("adding Request to monitors " + req.getId());
    if (!aRequests.contains(req)){
      aRequests.add(req);
      for (MonitorInterface monitor : monitors) {
        monitor.addWatchRequest(req);
      }
    }
  }

  @Override
  public void onRemoved(RdfBean object) {
    aRequests.remove(object);
  }

  private AsyncRequest findAsyncRequest(String uuid) {

    for (AsyncRequest i : aRequests) {
      if (i.getId().equals(uuid))
        return i;
    }
    
    return null;
    
  }
  
  private void assessRequest(AsyncRequest aRequest) {
   LOG.info("Assessing AsyncRequest "+aRequest.getId());
  }
  
  private void notify(AsyncRequest aRequest) {
    
  }
}
