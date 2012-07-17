package eu.scape_project.watch.scheduling.quartz;

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

	private Map<AdaptorPluginInterface, JobKey> adaptors;

	/**
	 * Default constructor
	 */
	public QuartzScheduler() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOG.error("Exception occured when creating a DefaultScheduler");
		}
	}

	@Override
	public void start(AdaptorPluginInterface adaptor, String id,
			Properties properties) {

		if (!adaptors.containsKey(id)) {

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
				
			}
			
			//store it to the map 
			adaptors.put(adaptor, jobDetail.getKey());
			
		} else {
			//TODO 
		}
	}

	@Override
	public void stop(AdaptorPluginInterface adaptor) {
		
		JobKey key = adaptors.get(adaptor);
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
		JobKey key = adaptors.get(adaptor);
		if (key!=null) {
			try {
				scheduler.resumeJob(key);
			} catch (SchedulerException e) {
				// TODO 
			}
		}else {
			//TODO
		}

	}

	@Override
	public void delete(AdaptorPluginInterface adaptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reschedule(AdaptorPluginInterface adaptor, Properties properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(AdaptorPluginInterface adaptor) {
		JobKey key = adaptors.get(adaptor);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void addAdaptorListener(AdaptorListenerInterface aListener,
			AdaptorPluginInterface adaptor) {
		// TODO Auto-generated method stub

	}

	public AdaptorPluginInterface getAdaptorPluginInterface(String id) {
		// TODO implement this
		return null;
	}

}
