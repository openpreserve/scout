package eu.scape_project.watch.core.listener;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.CentralMonitor;
import eu.scape_project.watch.core.CoreScheduler;

/**
 * 
 * @author kresimir
 *
 */
public class StartupListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		CoreScheduler cs = CoreScheduler.getCoreScheduler();
		try {
			cs.shutdown();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("Hello from Startup");
		CoreScheduler cs = CoreScheduler.getCoreScheduler();
		JobDetail job = newJob(CentralMonitor.class).withIdentity("centralMonitorJob","main group").build();
		Trigger trigger = newTrigger().withIdentity("myTrigger", "group1")
					.startNow().withSchedule(simpleSchedule()
					.withIntervalInSeconds(10)
					.repeatForever())            
			        .build();	
		try {
			cs.start();
			cs.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
