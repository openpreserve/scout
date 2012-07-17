package eu.scape_project.watch.scheduling.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class QuartzExecutionListener implements JobListener {

	private QuartzScheduler scheduler;
	
	private String name; 
	
	public QuartzExecutionListener() {
		
	}
	
	public QuartzExecutionListener(QuartzScheduler sc) {
		scheduler = sc;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		QuartzAdaptorJob job = (QuartzAdaptorJob)context.getJobInstance();
		job.setScheduler(scheduler);
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		
	}

}
