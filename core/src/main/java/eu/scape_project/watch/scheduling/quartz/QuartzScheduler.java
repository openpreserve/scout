package eu.scape_project.watch.scheduling.quartz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
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

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.domain.SourceAdaptorEventType;
import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.SchedulerInterface;
import eu.scape_project.watch.interfaces.SchedulerListenerInterface;
import eu.scape_project.watch.main.AssessmentService;

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

  private QuartzAdaptorCache adaptorCache;

  private final Map<String, JobKey> triggerToJobKeyMap;

  private QuartzListenerManager listenerManager;

  private QuartzExecutionListener executionListener;

  private List<SchedulerListenerInterface> schedulerListeners;

  /**
   * Default constructor
   */
  public QuartzScheduler(final AssessmentService assessmentService) {
    LOG.info("Creating QuartzScheduler");
    adaptorCache = new QuartzAdaptorCache();
    triggerToJobKeyMap = new HashMap<String, JobKey>();
    listenerManager = new QuartzListenerManager();
    executionListener = new QuartzExecutionListener();
    schedulerListeners = new ArrayList<SchedulerListenerInterface>();
    executionListener.setScheduler(this);
    executionListener.setListenerManager(listenerManager);
    executionListener.setAssessmentService(assessmentService);
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
    try {
      // Waiting for jobs to finish
      LOG.info("QuartzScheduler shutting down, waiting for all tasks to end...");
      // scheduler.shutdown(true);
      scheduler.shutdown(false);
      LOG.info("QuartzScheduler shutdown");
    } catch (SchedulerException e) {
      LOG.error("Failed to shut down QuartzScheduler, an exception occured " + e.getStackTrace());
    }
  }

  @Override
  public void startAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.STARTED);
    }

    if (!adaptorCache.containsAdaptor(adaptor)) {

      LOG.info("Starting the new adaptor plugin " + adaptor.getName());
      String id = adaptorCache.addAdaptorPlugin(adaptor);

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
          adaptorCache.addJobKey(adaptor, jobDetail.getKey());
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

    notifyAdaptorEvent(adaptor, event);

  }

  @Override
  public void stopAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.STOPPED);
    }

    JobKey key = adaptorCache.getAdaptorJobKey(adaptor);

    if (key != null) {
      try {
        scheduler.pauseJob(key);
        LOG.info("Adaptor " + adaptor.getName() + " successfully stopped");
        event.setSuccessful(true);
        event.setMessage("Adaptor " + adaptor.getName() + " successfully stopped");
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occured while stopping the adaptor " + adaptor.getName());
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

    notifyAdaptorEvent(adaptor, event);

  }

  @Override
  public void resumeAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.RESUMED);
    }

    JobKey key = adaptorCache.getAdaptorJobKey(adaptor);
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

    notifyAdaptorEvent(adaptor, event);

  }

  @Override
  public void deleteAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.DELETED);
    }

    JobKey key = adaptorCache.getAdaptorJobKey(adaptor);
    if (key != null) {
      try {
        scheduler.deleteJob(key);
        adaptorCache.removeAdaptorPlugin(adaptor);
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

    notifyAdaptorEvent(adaptor, event);

  }

  @Override
  public void executeAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {

    if (event == null) {
      event = new SourceAdaptorEvent();
      event.setType(SourceAdaptorEventType.EXECUTED);
    }

    JobKey key = adaptorCache.getAdaptorJobKey(adaptor);
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

    notifyAdaptorEvent(adaptor, event);

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
    AdaptorPluginInterface tmp = adaptorCache.getAdaptorPluginInterface(id);
    return tmp;
  }

  public void blockAdaptorPlugin(AdaptorPluginInterface adaptor) {
    adaptorCache.blockAdaptorPlugin(adaptor);
  }

  public void unblockAdaptorPlugin(AdaptorPluginInterface adaptor) {
    adaptorCache.unblockAdaptorPlugin(adaptor);
  }

  public boolean isAdaptorPluginBlocked(AdaptorPluginInterface adaptor) {
    return adaptorCache.isAdaptorPluginBlocked(adaptor);
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
  public void notifyAdaptorEvent(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {
    for (SchedulerListenerInterface sch : schedulerListeners) {
      sch.onSourceAdaptorEvent(adaptor, event);
    }
  }

  @Override
  public void startRequest(AsyncRequest request) {

    for (final eu.scape_project.watch.domain.Trigger trigger : request.getTriggers()) {
      startTrigger(trigger, request);
    }
    // TODO fire event
  }

  private void startTrigger(eu.scape_project.watch.domain.Trigger trigger, AsyncRequest request) {

    LOG.info("Starting trigger {}", trigger);
    final String id = trigger.getId();
    final long period = trigger.getPeriod();
    if (period > 0) {

      // create job detail
      final JobDetail jobDetail = JobBuilder.newJob(QuartzRequestTriggerJob.class).withIdentity(id, "triggers")
        .usingJobData("triggerId", id).usingJobData("requestId", request.getId()).build();

      // create trigger
      final Trigger qztrigger = TriggerBuilder.newTrigger().startNow()
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(period).repeatForever())
        .build();

      // schedule it
      try {
        LOG.info("Scheduling trigger {} to run every {}ms", new Object[] {id, period});
        scheduler.scheduleJob(jobDetail, qztrigger);
        // store the JobKey to the cache
        triggerToJobKeyMap.put(id, jobDetail.getKey());
        LOG.info("Request trigger {} is scheduled", id);
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occurred while starting the request trigger {}", trigger);
        // TODO log errors into KB.
      }
    } else {
      LOG.info("Trigger had no scheduling period: {}", trigger);
    }

  }

  @Override
  public void stopRequest(AsyncRequest request) {

    for (final eu.scape_project.watch.domain.Trigger trigger : request.getTriggers()) {
      stopTrigger(trigger, request);
    }
    // TODO fire event
  }

  private void stopTrigger(eu.scape_project.watch.domain.Trigger trigger, AsyncRequest request) {
    final JobKey key = triggerToJobKeyMap.get(trigger.getId());
    if (key != null) {
      try {
        scheduler.deleteJob(key);
        triggerToJobKeyMap.remove(trigger.getId());
      } catch (SchedulerException e) {
        LOG.error("A scheduler exception occurred while starting the request trigger {}", trigger);
        // TODO log errors into KB.
      }
    }
  }
}
