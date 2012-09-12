package eu.scape_project.watch.scheduling.quartz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import eu.scape_project.watch.interfaces.EventDetails;
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
  }

  @Override
  public void init() {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();
      scheduler.getListenerManager().addJobListener(executionListener, EverythingMatcher.allJobs());
      LOG.info("QuartzScheduler initialized");
    } catch (SchedulerException e) {
      LOG.error("An exception occured when initializing QuartzScheduler " + e.getStackTrace());
    }

  }

  @Override
  public void shutdown() {
    clear();
    try {
      scheduler.shutdown();
      LOG.info("QuartzScheduler shutdown");
    } catch (SchedulerException e) {
      LOG.error("Failed to shut down QuartzScheduler, an exception occured " + e.getStackTrace());
    }
  }

  @Override
  public void start(AdaptorPluginInterface adaptor, Map<String, String> properties, EventDetails details) {

    if (details==null) {
      details = new QuartzEventDetails();
    }
    
    if (!cache.containsAdaptor(adaptor)) {

      LOG.info("Starting the new adaptor plugin " + adaptor.getName());
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
              .withIntervalInSeconds(Integer.parseInt(properties.get("scheduler.intervalInSeconds"))).repeatForever())
          .build();

        // schedule it
        try {
          scheduler.scheduleJob(jobDetail, trigger);
          // store the JobKey to the cache
          cache.addJobKey(adaptor, jobDetail.getKey());
          LOG.info(adaptor.getName() + " is scheduled");
          details.setSuccessful(true);
          details.addMessage(adaptor.getName() + " is scheduled");
        } catch (SchedulerException e) {
          LOG.error("A scheduler exception occurred while starting the adaptor " + adaptor.getName());
          details.setSuccessful(false);
          details.addMessage("A scheduler exception occurred while starting the adaptor " + adaptor.getName());
        }

      } else {
        LOG.error("A problem occured when scheduling "+ adaptor.getName() +" it is not scheduled!");
        details.setSuccessful(false);
        details.addMessage("A problem occured when scheduling "+ adaptor.getName() +" it is not scheduled!");
      }
    } else {
      LOG.error(adaptor.getName() + " is already scheduled!");
      details.setSuccessful(false);
      details.addMessage(adaptor.getName() + " is already scheduled!");
    }
    
    notifyStart(adaptor, details);
  
  }

  @Override
  public void stop(AdaptorPluginInterface adaptor, EventDetails details) {
    
    if (details==null) {
      details = new QuartzEventDetails();
    }
    
    JobKey key = cache.getAdaptorJobKey(adaptor);
    
    if (key != null) {
      try {
        scheduler.pauseJob(key);
        LOG.info("Adaptor " + adaptor.getName() + " successfully stoped");
        details.setSuccessful(true);
        details.addMessage("Adaptor " + adaptor.getName() + " successfully stoped");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while stoping the adaptor "+ adaptor.getName());
        details.setSuccessful(false);
        details.addMessage("A scheduler exception occured while stoping the adaptor "+ adaptor.getName());
      }
    } else {
      LOG.error(adaptor.getName() + " unknown adaptor to stop");
      details.setSuccessful(false);
      details.addMessage(adaptor.getName() + " unknown adaptor to stop");
    }
    
    notifyStop(adaptor, details);
  
  }

  @Override
  public void resume(AdaptorPluginInterface adaptor, EventDetails details) {
    
    if (details==null) {
      details = new QuartzEventDetails();
    }
    
    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.resumeJob(key);
        LOG.info("Adaptor "+ adaptor.getName() +" successfully resumed");
        details.setSuccessful(true);
        details.addMessage("Adaptor "+ adaptor.getName() +" successfully resumed");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while resuming the adaptor "+ adaptor.getName());
        details.setSuccessful(false);
        details.addMessage("A scheduler exception occured while resuming the adaptor "+ adaptor.getName());
      }
    } else {
      LOG.error(adaptor.getName() + " unknown adaptor to resume");
      details.setSuccessful(false);
      details.addMessage(adaptor.getName() + " unknown adaptor to resume");
    }

    notifyResume(adaptor, details);
  
  }

  @Override
  public void delete(AdaptorPluginInterface adaptor, EventDetails details) {

    if (details==null) {
      details = new QuartzEventDetails();
    }
    
    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.deleteJob(key);
        cache.removeAdaptorPlugin(adaptor);
        LOG.info("Adaptor "+ adaptor.getName() +" successfully deleted");
        details.setSuccessful(true);
        details.addMessage("Adaptor "+ adaptor.getName() +" successfully deleted");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while deleting the adaptor "+ adaptor.getName());
        details.setSuccessful(false);
        details.addMessage("A scheduler exception occured while deleting the adaptor "+ adaptor.getName());
      }
    }else {
      LOG.error(adaptor.getName() + " unknown adaptor to delete");
      details.setSuccessful(false);
      details.addMessage(adaptor.getName() + " unknown adaptor to delete");
    }
    
    notifyDelete(adaptor, details);
  
  }


  @Override
  public void execute(AdaptorPluginInterface adaptor, EventDetails details) {

    if (details==null) {
      details = new QuartzEventDetails();
    }
    
    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.triggerJob(key);
        LOG.info("Adaptor "+ adaptor.getName() +" successfully executed");
        details.setSuccessful(true);
        details.addMessage("Adaptor "+ adaptor.getName() +" successfully executed");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while executing the adaptor "+ adaptor.getName());
        details.setSuccessful(false);
        details.addMessage("A scheduler exception occured while executing the adaptor "+ adaptor.getName());
      }
    } else {
      LOG.error(adaptor.getName() + " unknown adaptor to execute");
      details.setSuccessful(false);
      details.addMessage(adaptor.getName() + " unknown adaptor to execute");
    }

    notifyExecute(adaptor,details);
    
  }

  @Override
  public void clear() {
    try {
      scheduler.clear();
      LOG.info("All adaptors cleared from the scheduler");
    } catch (SchedulerException e) {
      LOG.info("A scheduler exception occured while clearing all adaptors");
    }

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

  private void notifyStart(AdaptorPluginInterface adaptor, EventDetails details) {
    for (SchedulerListenerInterface sch : schedulerListeners) {
      sch.adaptorPluginWasStarted(adaptor, details);
    }
  }

  private void notifyStop(AdaptorPluginInterface adaptor, EventDetails details) {
    for (SchedulerListenerInterface sch : schedulerListeners) {
      sch.adaptorPluginWasStoped(adaptor, details);
    }
  }

  private void notifyDelete(AdaptorPluginInterface adaptor, EventDetails details) {
    for (SchedulerListenerInterface sch : schedulerListeners) {
      sch.adaptorPluginWasDeleted(adaptor, details);
    }
  }

  private void notifyResume(AdaptorPluginInterface adaptor, EventDetails details) {
    for (SchedulerListenerInterface sch : schedulerListeners) {
      sch.adaptorPluginWasResumed(adaptor, details);
    }
  }

  private void notifyExecute(AdaptorPluginInterface adaptor, EventDetails details) {
    for (SchedulerListenerInterface sch : schedulerListeners) {
      sch.adaptorPluginWasExecuted(adaptor, details);
    }
  }

  @Override
  public void removeAdaptorListener(AdaptorListenerInterface aListener) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeSchedulerListener(SchedulerListenerInterface listener) {
    // TODO Auto-generated method stub

  }
}
