package eu.scape_project.watch.monitor;

import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.scheduling.CoreScheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionProfilerMonitor implements MonitorInterface {

  private static final Logger LOG = LoggerFactory.getLogger(CollectionProfilerMonitor.class);

  private static String GROUP_NAME = "CollectionProfileAdaptors";

  private CentralMonitor centralMonitor;

  private CoreScheduler coreScheduler;

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
    centralMonitor = cm;
  }

  @Override
  public String getGroup() {
    return this.GROUP_NAME;
  }

  @Override
  public void registerScheduler(CoreScheduler cs) {
    coreScheduler = cs;

  }

}
