package eu.scape_project.watch.components.listeners;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.CentralMonitor;
import eu.scape_project.watch.components.interfaces.IMonitor;

public class CollectionProfilerListener implements JobListener, IMonitor {

  private static final Logger LOG = LoggerFactory.getLogger(CollectionProfilerListener.class);
  
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "CollecetionProfileMonitor";
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
    LOG.info("Collection Profiler Monitor is executed");
    
  }

  @Override
  public void registerCentralMonitor(CentralMonitor cm) {
    // TODO Auto-generated method stub
    
  }

}
