package eu.scape_project.watch.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.RdfBean;

import eu.scape_project.watch.components.interfaces.IMonitor;
import eu.scape_project.watch.core.dao.AsyncRequestDAO;
import eu.scape_project.watch.core.dao.DOListener;

public class CentralMonitor implements DOListener {

  private static final Logger LOG = LoggerFactory.getLogger(CentralMonitor.class);

  private List<IMonitor> monitors;

  private AsyncRequestDAO asyncRequest; 
  
  public CentralMonitor() {
    monitors = new ArrayList<IMonitor>();
    LOG.info("CentralMonitor initialized");
  }

  public void addMonitor(IMonitor monitor) {
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
