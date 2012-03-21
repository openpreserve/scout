package eu.scape_project.watch.interfaces;

import org.quartz.JobListener;

import eu.scape_project.watch.monitor.CentralMonitor;
import eu.scape_project.watch.scheduling.CoreScheduler;

/**
 * Interface that will be implemented by all Monitor components in the system.
 * @author kresimir
 *
 */
public interface MonitorInterface extends JobListener {

  /**
   * Registering CentralMonitor instance to a monitor instance.  
   * @param cm - CentralMonitor
   */
  void registerCentralMonitor(CentralMonitor cm);
  
  /**
   * Register a CoreScheduler instance in to the Monitor instance. Soem Monitors will know how to reschedule AdaptorJob-s. 
   * @param cs - CoreScheduler
   */
  void registerScheduler(CoreScheduler cs);
  
  /**
   * Returns the name of a group that a specific Monitor monitors.  
   * @return
   */
  String getGroup();


}
