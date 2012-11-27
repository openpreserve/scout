package eu.scape_project.watch.scheduling.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.main.AssessmentService;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * Wraps up a request trigger.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class QuartzRequestTriggerJob implements Job {

  private static final Logger LOG = LoggerFactory.getLogger(QuartzRequestTriggerJob.class);

  private QuartzScheduler scheduler;

  private String requestId;

  private String triggerId;

  private QuartzListenerManager lManager;

  private AssessmentService assessmentService;

  public void setScheduler(QuartzScheduler scheduler) {
    this.scheduler = scheduler;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getTriggerId() {
    return triggerId;
  }

  public void setTriggerId(String triggerId) {
    this.triggerId = triggerId;
  }

  public void setlManager(QuartzListenerManager lManager) {
    this.lManager = lManager;
  }

  public AssessmentService getAssessmentService() {
    return assessmentService;
  }

  public void setAssessmentService(AssessmentService assessmentService) {
    this.assessmentService = assessmentService;
  }

  @Override
  public void execute(final JobExecutionContext jec) {
    LOG.info("Executing scheduled trigger {}", triggerId);

    final AsyncRequest request = DAO.ASYNC_REQUEST.findById(requestId);
    Trigger trigger = null;
    for (Trigger t : request.getTriggers()) {
      if (t.getId().equals(triggerId)) {
        trigger = t;
        break;
      }
    }
    if (trigger != null) {
      assessmentService.assess(trigger, request);
      jec.setResult(new Boolean(true));
    } else {
      jec.setResult(new Boolean(false));
    }

  }

}
