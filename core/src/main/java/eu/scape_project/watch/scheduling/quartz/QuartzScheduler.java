package eu.scape_project.watch.scheduling.quartz;

import java.util.Map;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorJobInterface;
import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.interfaces.SchedulerInterface;

/**
 * QuartzScheduler is an implementation of the SchedulerInterface. This implementation uses Quartz Scheduler 
 * (http://quartz-scheduler.org/) 
 * 
 * @author kresimir
 * 
 */
public class QuartzScheduler implements SchedulerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(QuartzScheduler.class);

  /**
   * Quartz Scheduler interface
   */
  private Scheduler scheduler;

  
  private Map<String, JobKey> adaptors; 
  
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
public void start(AdaptorPluginInterface adaptor, Properties properties) {

	
	
}

@Override
public void stop(AdaptorPluginInterface adptor) {
	// TODO Auto-generated method stub
	
}

@Override
public void resume(AdaptorPluginInterface adptor) {
	// TODO Auto-generated method stub
	
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
	// TODO Auto-generated method stub
	
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


public AdaptorPluginInterface getAdaptorPluginInterface(String id){
	//TODO implement this
	return null;
}

  
}



