package eu.scape_project.watch.scheduling.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class QuartzExecutionListener implements JobListener {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		// TODO Auto-generated method stub
		
	}

}
