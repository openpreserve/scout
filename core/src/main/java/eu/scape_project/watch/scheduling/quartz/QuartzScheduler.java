package eu.scape_project.watch.scheduling.quartz;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.SchedulerInterface;

/**
 * QuartzScheduler is an implementation of the SchedulerInterface. This
 * implementation uses Quartz Scheduler (http://quartz-scheduler.org/)
 * 
 * @author kresimir
 * 
 */
public class QuartzScheduler implements SchedulerInterface {

	private static final Logger LOG = LoggerFactory
			.getLogger(QuartzScheduler.class);

	/**
	 * Quartz Scheduler interface
	 */
	private Scheduler scheduler;

	private QuartzCache cache;	

	private QuartzListenerManager listenerManager;
	
	/**
	 * Default constructor
	 */
	public QuartzScheduler() {
		cache = new QuartzCache();
		listenerManager = new QuartzListenerManager();
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOG.error("Exception occured when creating a DefaultScheduler");
		}
	}

	@Override
	public void start(AdaptorPluginInterface adaptor,
			Properties properties) {

		if (!cache.containsAdaptor(adaptor)) {
			
			String id = cache.addAdaptorPlugin(adaptor);

			if (id != null) {
				// create job detail
				JobDetail jobDetail = JobBuilder.newJob(QuartzAdaptorJob.class)
					.withIdentity(id, "adaptors")
					.usingJobData("adaptorId", id).build();

				//create trigger
				Trigger trigger = TriggerBuilder.newTrigger().startNow().
					withSchedule(SimpleScheduleBuilder.simpleSchedule().
							withIntervalInMinutes(Integer.parseInt(properties.getProperty("scheduler.intervalInMinutes")))).
					build();

				//schedule it
				try {
					scheduler.scheduleJob(jobDetail, trigger);
				} catch (SchedulerException e) {
				 //TODO
				}
			
				//store the JobKey to the cache
				cache.addJobKey(adaptor, jobDetail.getKey());
			
			} else {
			//TODO id == null 
			}
		}else {
			//TODO cache contains adaptor
		}
	}

	@Override
	public void stop(AdaptorPluginInterface adaptor) {
		
		JobKey key = cache.getAdaptorJobKey(adaptor);
		if (key!=null) {
			try {
				scheduler.pauseJob(key);
			} catch (SchedulerException e) {
				// TODO 
			}
		}else {
			//TODO
		}

	}

	@Override
	public void resume(AdaptorPluginInterface adaptor) {
		JobKey key = cache.getAdaptorJobKey(adaptor);
		if (key!=null) {
			try {
				scheduler.resumeJob(key);
			} catch (SchedulerException e) {
				// TODO 
			}
		}else {
			// TODO
		}

	}

	@Override
	public void delete(AdaptorPluginInterface adaptor) {
		JobKey key = cache.getAdaptorJobKey(adaptor);
		if (key!=null) {
				try {
					scheduler.deleteJob(key);
					cache.removeAdaptorPlugin(adaptor);
				} catch (SchedulerException e) {
					// TODO
				}
		}

	}

	@Override
	public void reschedule(AdaptorPluginInterface adaptor, Properties properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(AdaptorPluginInterface adaptor) {
		JobKey key = cache.getAdaptorJobKey(adaptor);
		if (key!=null) {
			try {
				scheduler.triggerJob(key);
			} catch (SchedulerException e) {
				// TODO 
			}
		}else {
			//TODO
		}

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAdaptorListener(AdaptorListenerInterface aListener) {
		listenerManager.addAdaptorListener(aListener);
	}

	@Override
	public void addAdaptorListener(AdaptorListenerInterface aListener,
			AdaptorPluginInterface adaptor) {
		listenerManager.addAdaptorListener(aListener, adaptor);

	}

	public AdaptorPluginInterface getAdaptorPluginInterface(String id) {
		AdaptorPluginInterface tmp = cache.getAdaptorPluginInterface(id);
		return tmp;
	}

	public QuartzListenerManager getQuartzListenerManager() {
		return listenerManager;
	}
}
