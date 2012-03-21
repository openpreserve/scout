package eu.scape_project.watch.monitor;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.dao.AsyncRequestDAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.interfaces.MonitorInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.RdfBean;

public class CentralMonitor implements DOListener {

  private static final Logger LOG = LoggerFactory.getLogger(CentralMonitor.class);

  private List<MonitorInterface> monitors;

  private AsyncRequestDAO asyncRequest; 
  
  public CentralMonitor() {
    monitors = new ArrayList<MonitorInterface>();
    LOG.info("CentralMonitor initialized");
  }

  public void addMonitor(MonitorInterface monitor) {
    monitors.add(monitor);
    monitor.registerCentralMonitor(this);
  }

  public void registerToAsyncRequest(AsyncRequestDAO ar) {
    asyncRequest = ar;
    asyncRequest.addDOListener(this);
    LOG.info("CentralMonitor listening AsyncRequest");
  }
  
  @Override
  public void onUpdated(RdfBean object) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onRemoved(RdfBean object) {
    // TODO Auto-generated method stub

  }
}
