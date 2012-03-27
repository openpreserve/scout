package eu.scape_project.watch.interfaces;

import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * AdaptorJob interface is implemented by different Adaptors.
 * 
 * @author kresimir
 *
 */
public interface AdaptorJobInterface extends Job {
  
  
  /**
   * Setting the adaptor that a specific job should use. 
   * @param className - class name of the adaptor
   */
  void setAdaptorClassName(String className);
  
  /**
   * Getting the adaptor class name 
   * @return
   */
  String getAdaptorClassName();
  
  /**
   * Setting the version of the adaptor. 
   * @param version - version of the adaptor
   */
  void setAdaptorVersion(String version);
  
  /**
   * Getting the Adaptor version of installed adaptor plugin
   * @return
   */
  String getAdaptorVersion();
  
  /**
   * Setting adaptor properties 
   * @param properties
   */
  void setAdaptorProperties(String properties);
  
  /**
   * Returns the JobDetail of the AdaptorJob 
   * @return JobDetail
   */
  JobDetail getJobDetail();
  
  /**
   * Returns the Trigger of the AdaptorJob
   * @return
   */
  Trigger getTrigger();
  
  /**
   * Initialize AdaptorJob with Properties
   * @param properties
   */
  void initialize(Properties properties);
  
}
