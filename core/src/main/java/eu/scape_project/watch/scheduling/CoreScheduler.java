package eu.scape_project.watch.scheduling;

import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import eu.scape_project.watch.interfaces.AdaptorJobInterface;

/**
 * 
 * @author kresimir
 * 
 */
public class CoreScheduler {

  private Scheduler scheduler;

  public CoreScheduler() {
 // TODO throw exception in case of else
    if (scheduler == null) {
      try {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
      } catch (SchedulerException e) {
      }
    }
  }


  public void init(Scheduler sc) {
    // TODO throw exception in case of else
    if (scheduler == null) {
      scheduler = sc;
    }
  }

  public void start() {
    try {
      scheduler.start();
    } catch (SchedulerException e) {
     
    }
  }

  public void shutdown() {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {

    }
  }

  public void scheduleJob(JobDetail jobDetail, Trigger trigger) {
    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {

    }

  }

  public void scheduleAdaptorJob(AdaptorJobInterface job) {
    scheduleJob(job.getJobDetail(), job.getTrigger());
  }

  public void adddGroupJobListener(JobListener jobListener, String group) {
    try {
      scheduler.getListenerManager().addJobListener(jobListener, GroupMatcher.jobGroupEquals(group));
    } catch (SchedulerException e) {

    }
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

}
