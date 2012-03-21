package eu.scape_project.watch.interfaces;

import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface IAdaptorJob extends Job {
  
  public void setAdaptorClassName(String className);
  
  public void setAdaptorVersion(String version);
  
  public JobDetail getJobDetail();
  
  public Trigger getTrigger();
  
  public void initialize(Properties properties);
  
}
