package eu.scape_project.watch.scheduling.quartz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.SchedulerInterface;
import eu.scape_project.watch.interfaces.SchedulerListenerInterface;

/**
 * QuartzScheduler is an implementation of the SchedulerInterface. This
 * implementation uses Quartz Scheduler (http://quartz-scheduler.org/)
 * 
 * @author Kresimir Duretec <duretec@ifs.tuwien.ac.at>
 *
 */
public class QuartzScheduler implements SchedulerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(QuartzScheduler.class);

  /**
   * Quartz Scheduler interface
   */
  private Scheduler scheduler;

  private QuartzCache cache;

  private QuartzListenerManager listenerManager;

  private QuartzExecutionListener executionListener;

  private List<SchedulerListenerInterface> schedulerListeners;
 
  /**
   * Default constructor
   */
  public QuartzScheduler() {
	LOG.info("Creating QuartzScheduler");
    cache = new QuartzCache();
    listenerManager = new QuartzListenerManager();
    executionListener = new QuartzExecutionListener();
    schedulerListeners = new ArrayList<SchedulerListenerInterface>();
    executionListener.setScheduler(this);
    executionListener.setListenerManager(listenerManager);
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();
      scheduler.getListenerManager().addJobListener(executionListener, EverythingMatcher.allJobs());
    } catch (SchedulerException e) {
      LOG.error("Exception occured when creating a DefaultScheduler");
    }
  }

  @Override
  public void start(AdaptorPluginInterface adaptor, Map<String, String> properties) {

    if (!cache.containsAdaptor(adaptor)) {

      LOG.info("Scheduling new adaptor plugin " + adaptor.getName());
      String id = cache.addAdaptorPlugin(adaptor);

      if (id != null) {
        // create job detail
        JobDetail jobDetail = JobBuilder.newJob(QuartzAdaptorJob.class).withIdentity(id, "adaptors")
          .usingJobData("adaptorId", id).build();

        // create trigger
        Trigger trigger = TriggerBuilder
          .newTrigger()
          .startNow()
          .withSchedule(
            SimpleScheduleBuilder.simpleSchedule()
              .withIntervalInSeconds(Integer.parseInt(properties.get("scheduler.intervalInSeconds")))
              .repeatForever()).build();

        // schedule it
        try {
          scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
          // TODO
          e.printStackTrace();
          LOG.error("An error occurred: {}", e.getMessage());
          
          // aaaaaaaaaaaaaarrrgh
          // Please add a simple Log or print the stack trace at least.
          // it is a one liner, saves a lot of debugging time and
          // makes sure people do not get nervous breakdowns!!!!!
        }

        // store the JobKey to the cache
        cache.addJobKey(adaptor, jobDetail.getKey());
        notifyStart(adaptor);  
        LOG.info(adaptor.getName() + " scheduled");
      } else {
        LOG.error(adaptor.getName() + "problem when scheduling adaptor - it can not be scheduled!");
      }
    } else {
       LOG.error(adaptor.getName() + " is already scheduled!");
    }
  }

  @Override
  public void stop(AdaptorPluginInterface adaptor) {

    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.pauseJob(key);
        notifyStop(adaptor);
      } catch (SchedulerException e) {
        e.printStackTrace();
      }
    } else {
      LOG.error(adaptor.getName()+ " unknown adaptor to stop");
    }
  }

  @Override
  public void resume(AdaptorPluginInterface adaptor) {
    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.resumeJob(key);
        notifyResume(adaptor);
      } catch (SchedulerException e) {
        e.printStackTrace();
      }
    } else {
      LOG.error(adaptor.getName()+ " unknown adaptor to resume");
    }
  }

  @Override
  public void delete(AdaptorPluginInterface adaptor) {
    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.deleteJob(key);
        notifyDelete(adaptor);
        cache.removeAdaptorPlugin(adaptor);
      } catch (SchedulerException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void reschedule(AdaptorPluginInterface adaptor, Map<String, String> properties) {
    // TODO Auto-generated method stub

  }

  @Override
  public void execute(AdaptorPluginInterface adaptor) {
    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.triggerJob(key);
      } catch (SchedulerException e) {
        // TODO
        e.printStackTrace();
        LOG.error("An error occurred: " + e.getMessage());
      }
    } else {
      LOG.error(adaptor.getName()+ " unknown adaptor to execute");
    }

  }

  @Override
  public void clear() {
    LOG.info("Clearing all adaptors");

  }

  @Override
  public void addAdaptorListener(AdaptorListenerInterface aListener) {
    listenerManager.addAdaptorListener(aListener);
  }

  @Override
  public void addAdaptorListener(AdaptorListenerInterface aListener, AdaptorPluginInterface adaptor) {
    listenerManager.addAdaptorListener(aListener, adaptor);
  }

  @Override
  public void addSchedulerListener(SchedulerListenerInterface listener) {
    schedulerListeners.add(listener);
  }

  public AdaptorPluginInterface getAdaptorPluginInterface(String id) {
    AdaptorPluginInterface tmp = cache.getAdaptorPluginInterface(id);
    return tmp;
  }

  public QuartzListenerManager getQuartzListenerManager() {
    return listenerManager;
  }

  private void notifyStart(AdaptorPluginInterface adaptor){
    for (SchedulerListenerInterface sch : schedulerListeners){
      sch.adaptorPluginWasStarted(adaptor);
    }
  }
  
  private void notifyStop(AdaptorPluginInterface adaptor){
    for (SchedulerListenerInterface sch : schedulerListeners){
      sch.adaptorPluginWasStoped(adaptor);
    }
  }
  
  private void notifyDelete(AdaptorPluginInterface adaptor){
    for (SchedulerListenerInterface sch : schedulerListeners){
      sch.adaptorPluginWasDeleted(adaptor);
    }
  }
  
  private void notifyResume(AdaptorPluginInterface adaptor){
    for (SchedulerListenerInterface sch : schedulerListeners){
      sch.adaptorPluginWasResumed(adaptor);
    }
  }
  
  private void notifyReschedule(AdaptorPluginInterface adaptor){
    for (SchedulerListenerInterface sch : schedulerListeners){
      sch.adaptorPluginWasRescheduled(adaptor);
    }
  }
}
