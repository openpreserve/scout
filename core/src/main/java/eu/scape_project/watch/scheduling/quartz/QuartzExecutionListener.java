package eu.scape_project.watch.scheduling.quartz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import eu.scape_project.watch.interfaces.AdaptorPluginInterface;

public class QuartzExecutionListener implements JobListener {

	private int REPEAT = 5;
	
	private QuartzScheduler scheduler;
	
	private String name = "QuartzExecutionListener"; 
	
	private Map<AdaptorPluginInterface, Integer> failed;
	
	public QuartzExecutionListener() {
		failed = new HashMap<AdaptorPluginInterface, Integer>();
	}
	
	public QuartzExecutionListener(QuartzScheduler sc) {
		scheduler = sc;
	}
	
	public void setScheduler(QuartzScheduler sc) {
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
		QuartzAdaptorJob job = (QuartzAdaptorJob)context.getJobInstance();
		AdaptorPluginInterface adaptor = job.getAdaptorPlugin();
		if (jobException == null){
			failed.remove(adaptor);
		}else {
			int num;
			if (failed.containsKey(adaptor)){
				Integer i = failed.get(adaptor);
				i = i + 1;
				num = i.intValue();
				failed.put(adaptor, i);
			}else {
				failed.put(adaptor, new Integer(1));
				num = 1;
			}
			if (num>REPEAT){
				scheduler.stop(adaptor);
				failed.remove(adaptor);
			}
		}
		
		
	}

}
