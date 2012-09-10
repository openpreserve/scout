package eu.scape_project.watch.scheduling.quartz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorPluginInterface;

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
    LOG.info(adaptor.getName() + " was executed");
    Boolean result = (Boolean) context.getResult();
    if (result.booleanValue() == true) {
      failed.remove(adaptor);
    } else {
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
        LOG.warn("Unscheduling adaptor: "+adaptor.getName());
        scheduler.stop(adaptor);
        failed.remove(adaptor);
      } else {
        LOG.warn("Refiring adaptor: "+adaptor.getName());
        scheduler.execute(adaptor);
      }
    }

  }

}
