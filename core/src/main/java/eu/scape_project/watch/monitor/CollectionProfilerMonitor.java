package eu.scape_project.watch.monitor;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.dao.PropertyValueDAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.AdaptorJobInterface;
import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.scheduling.CoreScheduler;

public class CollectionProfilerMonitor implements MonitorInterface {

  private static final Logger LOG = LoggerFactory.getLogger(CollectionProfilerMonitor.class);

  private static String GROUP_NAME = "CollectionProfileAdaptors";

  private CentralMonitor centralMonitor;

  private CoreScheduler coreScheduler;

  private List<String> aRequestUUIDS;

  private List<AdaptorJobInterface> adaptorJobs;

  private EntityType monitorType;

  public CollectionProfilerMonitor() {

    aRequestUUIDS = new ArrayList<String>();
    adaptorJobs = new ArrayList<AdaptorJobInterface>();
    monitorType = new EntityType("CollectionProfile", "");

  }

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
    AdaptorJobInterface aJob = (AdaptorJobInterface) arg0.getJobInstance();
    if (!adaptorJobs.contains(aJob)) {
      adaptorJobs.add(aJob);
      LOG.info("Monitor found and stored new AdaptorJob");
    }
  }

  @Override
  public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
    LOG.info("Collection Profiler Monitor is executed");
    ResultInterface result = (ResultInterface) arg0.getResult();
    List<PropertyValue> rValues = result.getPropertyValues();
    for (PropertyValue pv : rValues) {
      Entity e = pv.getEntity();
      Property p = pv.getProperty();
      EntityDAO.getInstance().save(e);
      PropertyDAO.getInstance().save(p);

      PropertyValueDAO.getInstance().save(pv);
      // LOG.info("property value {} - {}",
      // pv.getProperty().getName(),pv.getValue());
    }

    LOG.info("Monitor found {} values", rValues.size());
    // TODO add notification

    centralMonitor.notifyAsyncRequests(aRequestUUIDS);

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

  @Override
  public void addWatchRequest(AsyncRequest aRequest) {

    if (isRequestSupported(aRequest))
      aRequestUUIDS.add(aRequest.getId());

  }

  @Override
  public void removeWatchRequest(AsyncRequest aRequest) {
    // TODO Auto-generated method stub

  }

  private boolean isRequestSupported(AsyncRequest aRequest) {

    LOG.info("checking");
    for (Trigger trigger : aRequest.getTriggers()) {
      List<EntityType> eType = trigger.getQuestion().getTypes();
      LOG.info("hello");
      LOG.info(eType.toString());
      LOG.info("size "+eType.size());
      if (eType.contains(monitorType)) {
        LOG.info("request is supported");
        return true;
      }
    }
    LOG.info("request is not supported");
    return false;

  }
}
