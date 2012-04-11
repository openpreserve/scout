package eu.scape_project.watch.monitor;

import eu.scape_project.watch.dao.DAO;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public Collection<String> getAsyncRequestUUIDS() {
    return Collections.unmodifiableCollection(aRequestUUIDS);
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
      LOG.debug("Monitor found and stored new AdaptorJob");
    }
  }

  @Override
  public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
    LOG.info("Collection Profiler Monitor is executed");
    ResultInterface result = (ResultInterface) arg0.getResult();
    // TODO this part needs to be improved with exceptions
    if (result != null) {
      List<PropertyValue> rValues = result.getPropertyValues();
      for (PropertyValue pv : rValues) {
        Entity e = pv.getEntity();
        Property p = pv.getProperty();
        DAO.save(e);
        DAO.save(p);

        DAO.save(pv);
        // LOG.info("property value {} - {}",
        // pv.getProperty().getName(),pv.getValue());
      }
      LOG.debug("Monitor found {} values", rValues.size());
      centralMonitor.notifyAsyncRequests(aRequestUUIDS);
    } else {
      LOG.warn("SKIPPING - CollectionProfilerMonitor recived null as a result from an AdabtorJob");
    }

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

    if (!aRequestUUIDS.contains(aRequest.getId())) {
      if (isRequestSupported(aRequest))
        aRequestUUIDS.add(aRequest.getId());
    }

  }

  @Override
  public void removeWatchRequest(AsyncRequest aRequest) {

    aRequestUUIDS.remove(aRequest.getId());

  }

  private boolean isRequestSupported(AsyncRequest aRequest) {

    for (Trigger trigger : aRequest.getTriggers()) {
      List<EntityType> eType = trigger.getQuestion().getTypes();
      if (eType.contains(monitorType))
        return true;
    }
    return false;

  }
}
