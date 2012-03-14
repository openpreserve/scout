package eu.scape_project.watch.core;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * @author kresimir
 *
 */
public class CoreScheduler {

	private static CoreScheduler coreScheduler;
	
	private Scheduler scheduler ;
	
	public static CoreScheduler getCoreScheduler() {
		if (coreScheduler==null)
			coreScheduler = new CoreScheduler();
		return coreScheduler;
	}
	
	public void init()  {
		// TODO  throw exception in case of else
		if (scheduler==null) { 
			try {
				scheduler = StdSchedulerFactory.getDefaultScheduler();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void init(Scheduler sc){
		//TODO throw exception in case of else
		if (scheduler==null)
			scheduler = sc;
	}
	
	public void start() throws SchedulerException {
		scheduler.start();
	}
	
	public void shutdown() throws SchedulerException{
		scheduler.shutdown();
	}
	public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	
	private CoreScheduler () {
		init();
	}
}
