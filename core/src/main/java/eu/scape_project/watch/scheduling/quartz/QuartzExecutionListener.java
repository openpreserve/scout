package eu.scape_project.watch.scheduling.quartz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.domain.SourceAdaptorEventType;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.main.AssessmentService;
import eu.scape_project.watch.utils.exceptions.PluginException;

public class QuartzExecutionListener implements JobListener {

  private static final Logger LOG = LoggerFactory.getLogger(QuartzExecutionListener.class);

  private int REPEAT = 5;

  private QuartzScheduler scheduler;

  private QuartzListenerManager listenerManager;

  private AssessmentService assessmentService;

  private String name = "QuartzExecutionListener";

  private Map<AdaptorPluginInterface, Integer> failed;

  public QuartzExecutionListener() {
    failed = new HashMap<AdaptorPluginInterface, Integer>();
  }

  public QuartzExecutionListener(QuartzScheduler sc) {
    scheduler = sc;
  }

  public void setScheduler(QuartzScheduler sc) {
    scheduler = sc;
  }

  public void setListenerManager(QuartzListenerManager lm) {
    listenerManager = lm;
  }

  public void setAssessmentService(AssessmentService assessmentService) {
    this.assessmentService = assessmentService;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    final Job job = context.getJobInstance();

    if (job instanceof QuartzAdaptorJob) {
      final QuartzAdaptorJob adaptorJob = (QuartzAdaptorJob) job;
      adaptorJob.setScheduler(scheduler);
      adaptorJob.setlManager(listenerManager);
    } else if (job instanceof QuartzRequestTriggerJob) {
      final QuartzRequestTriggerJob triggerJob = (QuartzRequestTriggerJob) job;
      triggerJob.setScheduler(scheduler);
      triggerJob.setlManager(listenerManager);
      triggerJob.setAssessmentService(assessmentService);
    }

  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {

  }

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

    final Job jobInstance = context.getJobInstance();

    if (jobInstance instanceof QuartzAdaptorJob) {
      Boolean skip = (Boolean) context.get("skip");

      if (!skip.booleanValue()) {
        QuartzAdaptorJob job = (QuartzAdaptorJob) jobInstance;
        AdaptorPluginInterface adaptor = job.getAdaptorPlugin();
        Boolean result = (Boolean) context.getResult();
        if (result.booleanValue()) {
          failed.remove(adaptor);
          LOG.info(adaptor.getName() + " was successfully executed");
          SourceAdaptorEvent event = new SourceAdaptorEvent();
          event.setType(SourceAdaptorEventType.EXECUTED);
          event.setSuccessful(true);
          event.setMessage(adaptor.getName() + " was successfully executed");
          scheduler.notifyAdaptorEvent(adaptor, event);
        } else {
          Throwable e = (Throwable) context.get("exception");
          LOG.warn(adaptor.getName() + " was not successfully executed. An exception happened: " + e.getMessage(), e);
          SourceAdaptorEvent event = new SourceAdaptorEvent();
          event.setType(SourceAdaptorEventType.EXECUTED);
          event.setSuccessful(false);
          event.setMessage(adaptor.getName() + " was not successfully executed");
          event.setReason("An exception happened: " + printException(e));
          scheduler.notifyAdaptorEvent(adaptor, event);
          int num;
          if (failed.containsKey(adaptor)) {
            Integer i = failed.get(adaptor);
            i = i + 1;
            num = i.intValue();
            failed.put(adaptor, i);
          } else {
            failed.put(adaptor, Integer.valueOf(1));
            num = 1;
          }
          if (num > REPEAT) {
            LOG.warn("Unscheduling adaptor: " + adaptor.getName());
            SourceAdaptorEvent event2 = new SourceAdaptorEvent();
            event2.setReason("Adaptor failed 5 times in a row");
            scheduler.stopAdaptor(adaptor, event2);
            failed.remove(adaptor);
          } else {
            LOG.warn("Refiring adaptor: " + adaptor.getName());
            SourceAdaptorEvent event3 = new SourceAdaptorEvent();
            event3.setReason("Adaptor failed to execute so it will be reexecuted immediately");
            scheduler.executeAdaptor(adaptor, event3);
          }
        }
      } else {
        LOG.warn("Nothing to do , Adaptor execution was skipped");
      }
    }

  }

  private String printException(Throwable e) {
    final StringBuilder ret = new StringBuilder();
    if (e != null) {
      ret.append(e.getMessage());
      Throwable cause = e.getCause();
      while (cause != null) {
        ret.append(" cause: [");
        ret.append(e.getCause().getClass());
        ret.append("] ");
        ret.append(e.getCause().getMessage());
        cause = cause.getCause();
      }
    }
    return ret.toString();
  }

}
