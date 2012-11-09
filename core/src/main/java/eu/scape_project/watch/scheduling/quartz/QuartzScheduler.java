package eu.scape_project.watch.scheduling.quartz;

import java.util.ArrayList;
import java.util.List;

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

import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.domain.SourceAdaptorEventType;
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
   * The period of monitoring in seconds.
   * 
   * TODO this should be defined in external configuration or per adaptor.
   */
  private static final int SCHEDULE_INTERVAL_IN_SECONDS = 3600; // 1 hour

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
  public void start(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.STARTED);
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
            SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(SCHEDULE_INTERVAL_IN_SECONDS).repeatForever())
          .build();

        // schedule it
        try {
          scheduler.scheduleJob(jobDetail, trigger);
          // store the JobKey to the cache
          cache.addJobKey(adaptor, jobDetail.getKey());
          LOG.info(adaptor.getName() + " is scheduled");
          event.setSuccessful(true);
          event.setMessage(adaptor.getName() + " is scheduled");
        } catch (SchedulerException e) {
          LOG.error("A scheduler exception occurred while starting the adaptor " + adaptor.getName());
          event.setSuccessful(false);
          event.setMessage("Error occurred while starting the adaptor " + adaptor.getName());
          event.setReason("Exception: " + e);
        }

      } else {
        LOG.error("A problem occured when scheduling " + adaptor.getName() + " it is not scheduled!");
        event.setSuccessful(false);
        event.setMessage("A problem occured when scheduling " + adaptor.getName());
        event.setReason("Adaptor is not scheduled");
      }
    } else {
      LOG.error(adaptor.getName() + " is already scheduled!");
      event.setSuccessful(false);
      event.setMessage("Can't start adaptor " + adaptor.getName());
      event.setReason("Adaptor is already scheduled!");
    }

    notifyEvent(adaptor, event);

  }

  @Override
  public void stop(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.STOPPED);
    }

    JobKey key = cache.getAdaptorJobKey(adaptor);

    if (key != null) {
      try {
        scheduler.pauseJob(key);
        LOG.info("Adaptor " + adaptor.getName() + " successfully stoped");
        event.setSuccessful(true);
        event.setMessage("Adaptor " + adaptor.getName() + " successfully stoped");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while stoping the adaptor " + adaptor.getName());
        event.setSuccessful(false);
        event.setMessage("Could not stop adaptor " + adaptor.getName());
        event.setReason("Exception: " + e);
      }
    } else {
      LOG.error(adaptor.getName() + " unknown adaptor to stop");
      event.setSuccessful(false);
      event.setMessage("Could not stop adaptor " + adaptor.getName());
      event.setReason("Adaptor " + adaptor.getName() + " is unknown");
      event.setMessage(adaptor.getName() + " unknown adaptor to stop");
    }

    notifyEvent(adaptor, event);

  }

  @Override
  public void resume(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.RESUMED);
    }

    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.resumeJob(key);
        LOG.info("Adaptor " + adaptor.getName() + " successfully resumed");
        event.setSuccessful(true);
        event.setMessage("Adaptor " + adaptor.getName() + " successfully resumed");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while resuming the adaptor " + adaptor.getName());
        event.setSuccessful(false);
        event.setMessage("Could not resume adaptor " + adaptor.getName());
        event.setReason("Exception: " + e);
      }
    } else {
      LOG.error(adaptor.getName() + " unknown adaptor to resume");
      event.setSuccessful(false);
      event.setMessage("Could not resume adaptor " + adaptor.getName());
      event.setReason("Adaptor " + adaptor.getName() + " is unknown");
    }

    notifyEvent(adaptor, event);

  }

  @Override
  public void delete(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.DELETED);
    }

    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.deleteJob(key);
        cache.removeAdaptorPlugin(adaptor);
        LOG.info("Adaptor " + adaptor.getName() + " successfully deleted");
        event.setSuccessful(true);
        event.setMessage("Adaptor " + adaptor.getName() + " successfully deleted");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while deleting the adaptor " + adaptor.getName());
        event.setSuccessful(false);
        event.setMessage("A scheduler exception occured while deleting the adaptor " + adaptor.getName());
        event.setReason("Exception: " + e);
      }
    } else {
      LOG.error(adaptor.getName() + " unknown adaptor to delete");
      event.setSuccessful(false);
      event.setMessage("Can't delete adaptor " + adaptor.getName());
      event.setReason("Adaptor " + adaptor.getName() + " is unknown");
    }

    notifyEvent(adaptor, event);

  }

  @Override
  public void execute(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.EXECUTED);
    }

    JobKey key = cache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.triggerJob(key);
        LOG.info("Adaptor " + adaptor.getName() + " refired");
        event.setSuccessful(true);
        event.setMessage("Adaptor " + adaptor.getName() + " refired");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while refiring the adaptor " + adaptor.getName());
        event.setSuccessful(false);
        event.setMessage("A scheduler exception occured while refiring the adaptor " + adaptor.getName());
        event.setReason("Exception: " + e);
      }
    } else {
      LOG.error(adaptor.getName() + " unknown adaptor to refire");
      event.setSuccessful(false);
      event.setMessage("Can't refire adaptor " + adaptor.getName());
      event.setReason("Adaptor " + adaptor.getName() + " is unknown");
    }

    notifyEvent(adaptor, event);

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
  public void removeAdaptorListener(AdaptorListenerInterface aListener) {
    listenerManager.removeAdaptorListener(aListener);
  }

  @Override
  public void addSchedulerListener(SchedulerListenerInterface listener) {
    schedulerListeners.add(listener);
  }

  @Override
  public void removeSchedulerListener(SchedulerListenerInterface listener) {
    schedulerListeners.remove(listener);
  }

  public AdaptorPluginInterface getAdaptorPluginInterface(String id) {
    AdaptorPluginInterface tmp = cache.getAdaptorPluginInterface(id);
    return tmp;
  }
  
  public void blockAdaptorPlugin(AdaptorPluginInterface adaptor) {
    cache.blockAdaptorPlugin(adaptor);
  }
  
  public void unblockAdaptorPlugin(AdaptorPluginInterface adaptor) {
    cache.unblockAdaptorPlugin(adaptor);
  }
  
  public boolean isAdaptorPluginBlocked(AdaptorPluginInterface adaptor) {
    return cache.isAdaptorPluginBlocked(adaptor);
  }

  public QuartzListenerManager getQuartzListenerManager() {
    return listenerManager;
  }

  /**
   * This method is public because execution event can happen outside of the
   * QuartzScheduler class.
   * 
   * @param adaptor
   *          The adaptor that produced the event.
   * @param event
   *          Details of the event.
   */
  public void notifyEvent(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {
    for (SchedulerListenerInterface sch : schedulerListeners) {
      sch.onEvent(adaptor, event);
    }
  }

}
