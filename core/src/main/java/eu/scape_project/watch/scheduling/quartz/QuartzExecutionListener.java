package eu.scape_project.watch.scheduling.quartz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.utils.exceptions.PluginException;

public class QuartzExecutionListener implements JobListener {

  private static final Logger LOG = LoggerFactory.getLogger(QuartzExecutionListener.class);

  private int REPEAT = 5;

  private QuartzScheduler scheduler;

  private QuartzListenerManager listenerManager;

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

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    QuartzAdaptorJob job = (QuartzAdaptorJob) context.getJobInstance();
    job.setScheduler(scheduler);
    job.setlManager(listenerManager);
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {

  }

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    QuartzAdaptorJob job = (QuartzAdaptorJob) context.getJobInstance();
    AdaptorPluginInterface adaptor = job.getAdaptorPlugin();
    Boolean result = (Boolean) context.getResult();
    if (result != null && result.booleanValue()) {
      failed.remove(adaptor);
      LOG.info(adaptor.getName() + " was successfully executed");
      SourceAdaptorEvent event = new SourceAdaptorEvent();
      event.setSuccessful(true);
      event.setMessage(adaptor.getName() + " was successfully executed");
      scheduler.notifyExecute(adaptor, event);
    } else {
      PluginException e = (PluginException) context.get("exception");
      LOG.warn(adaptor.getName() + " was not successfully executed. An exception happened: " + e);
      SourceAdaptorEvent event = new SourceAdaptorEvent();
      event.setSuccessful(false);
      event.setMessage(adaptor.getName() + " was not successfully executed");
      event.setReason("An exception happened: " + e);
      scheduler.notifyExecute(adaptor, event);
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
        scheduler.stop(adaptor, event2);
        failed.remove(adaptor);
      } else {
        LOG.warn("Refiring adaptor: " + adaptor.getName());
        SourceAdaptorEvent event3 = new SourceAdaptorEvent();
        event3.setReason("Adaptor failed to execute so it will be reexecuted immediately");
        scheduler.execute(adaptor, event3);
      }
    }

  }

}
