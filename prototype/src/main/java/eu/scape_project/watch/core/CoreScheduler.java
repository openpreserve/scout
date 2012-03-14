package eu.scape_project.watch.core;

import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import eu.scape_project.watch.components.listeners.CollectionProfilerListener;

/**
 * 
 * @author kresimir
 * 
 */
public class CoreScheduler {

  private static CoreScheduler coreScheduler;

  private Scheduler scheduler;

  public static CoreScheduler getCoreScheduler() {
    if (coreScheduler == null)
      coreScheduler = new CoreScheduler();
    return coreScheduler;
  }

  public void init() {
    // TODO throw exception in case of else
    if (scheduler == null) {
      try {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
      } catch (SchedulerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void init(Scheduler sc) {
    // TODO throw exception in case of else
    if (scheduler == null)
      scheduler = sc;
  }

  public void start()  {
    try {
      scheduler.start();
    } catch (SchedulerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void shutdown()  {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void scheduleJob(JobDetail jobDetail, Trigger trigger) {
    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void adddGroupJobListener(JobListener jobListener, String group) {
    try {
      scheduler.getListenerManager().addJobListener(jobListener, GroupMatcher.jobGroupEquals(group));
    } catch (SchedulerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  private CoreScheduler() {
    init();
  }
}
