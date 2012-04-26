package eu.scape_project.watch.scheduling;

import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorJobInterface;
import eu.scape_project.watch.interfaces.MonitorInterface;

/**
 * This class acts as an adaptor to the functionality provided by a Quartz
 * Scheduler. It offers methods that are needed by other classes for scheduling
 * jobs. Other classes should use this class for scheduling new jobs or
 * rescheduling already existing ones and not directly Scheduler class.
 * 
 * @author kresimir
 * 
 */
public class CoreScheduler {

  private static final Logger LOG = LoggerFactory.getLogger(CoreScheduler.class);

  /**
   * Quartz Scheduler interface
   */
  private Scheduler scheduler;

  /**
   * Default constructor
   */
  public CoreScheduler() {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
    } catch (SchedulerException e) {
      LOG.error("Exception occured when creating a DefaultScheduler");
    }
  }

  /**
   * Set a Scheduler.
   * @param sc - Scheduler
   */
  public void setScheduler(Scheduler sc) {
      scheduler = sc;
  }

  /**
   * Get a Scheduler.
   * @return Scheduler
   */
  public Scheduler getScheduler() {
    return scheduler;
  }

  /**
   * Start the Scheduler. 
   */
  public void start() {
    try {
      scheduler.start();
    } catch (SchedulerException e) {
      LOG.error("Unable to start the scheduler");
    }
  }

  /**
   * Shutdown the Scheduler.
   */
  public void shutdown() {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      LOG.error("Unable to shutdown the scheduler");
    }
  }

  /**
   * Clear all scheduled Jobs.
   */
  public void clear() {
    try {
      scheduler.clear();
    } catch (SchedulerException e) {
      LOG.error("Cannot clear the Scheduler");
    }
  }
  
  
  /**
   * Schedule an new job providing JobDetail and Trigger. 
   * @param jobDetail
   * @param trigger
   */
  public void scheduleJob(JobDetail jobDetail, Trigger trigger) {
    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      LOG.error("Scheduler failed to schedule a job");
    }
  }

  /**
   * Schedule an AdaptorJob. 
   * @param job - AdaptorJob to be scheduled
   */
  public void scheduleAdaptorJob(AdaptorJobInterface job) {
    scheduleJob(job.getJobDetail(), job.getTrigger());
  }

  /**
   * Add a JobListener for a specific group.
   * @param jobListener - JobListener
   * @param group - name of the group to be monitored
   */
  public void addGroupJobListener(JobListener jobListener, String group) {
    try {
      scheduler.getListenerManager().addJobListener(jobListener, GroupMatcher.jobGroupEquals(group));
    } catch (SchedulerException e) {
      LOG.error("Could not add Monitor to Scheduler Listeners");
    }
  }

  /**
   * Register  a Monitor to be a specific listener for a group of Jobs.
   * @param monitor - MonitorInterface
   */
  public void registerMonitor(MonitorInterface monitor) {
    addGroupJobListener(monitor,monitor.getGroup());
  }

  /**
   * Reschedules a Trigger with a new Trigger
   * @param oldTrigger
   * @param newTrigger
   */
  public void rescheduleExistingJob(Trigger oldTrigger, Trigger newTrigger) {
    try {
      scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
    } catch (SchedulerException e) {
      LOG.error("Cannot reschedule Trigger");
    }
  }
  
  /**
   * Adds another trigger to a Job
   * @param trigger
   */
  public void addTrigger(Trigger trigger) {
    try {
      scheduler.scheduleJob(trigger);
    } catch (SchedulerException e) {
      LOG.error("Cannot add new Trigger to a Scheduler"); 
    }
  }
}



