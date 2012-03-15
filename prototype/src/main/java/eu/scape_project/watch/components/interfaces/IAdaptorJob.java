package eu.scape_project.watch.components.interfaces;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import eu.scape_project.watch.components.elements.Result;

public interface IAdaptorJob extends Job {
  
  public void setAdaptor(String className);
  
  public JobDetail getJobDetail();
  
  public Trigger getTrigger();
  
}
