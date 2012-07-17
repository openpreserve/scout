package eu.scape_project.watch.interfaces;

import java.util.Properties;

import eu.scape_project.watch.interfaces.AdaptorPluginInterface;

/**
 * This interface is responsible for providing methods for scheduling adaptors. 
 * @author kresimir
 *
 */
public interface SchedulerInterface {

  /**
   * Method which is responsible to start an adaptor. Possible scheduling parameters like execution interval 
   * or similar are passed as an argument. To find out which parameters are needed check specific implementations
   * of this interface.   
   * @param adaptor - AdaptorPluginInterface to be started 
   * @param properties - properties needed to successfully start an adaptor
   */
  void start(AdaptorPluginInterface adaptor, Properties properties);

  /**
   * Method responsible to stop an already running adaptor. Once paused adaptor can be only started again with 
   * resume method. 
   * @param adptor -  adaptor to be stopped
   */
  void stop(AdaptorPluginInterface adptor); 
  
  /**
   * Method responsible for resuming stopped adaptor. 
   * @param adptor - adaptor to be resumed 
   */
  void resume(AdaptorPluginInterface adptor); 
  
  /**
   * Method for deleting specific adaptor. 
   * @param adaptor - adaptor to be deleted 
   */
  void delete(AdaptorPluginInterface adaptor);
  /**
   * Method responsible for rescheduling an adaptor. The running adaptor can be rescheduled by providing new properties.  
   * @param adaptor - adaptor to be rescheduled 
   */
  void reschedule(AdaptorPluginInterface adaptor, Properties properties);
  
  /**
   * Method which allows immediate adaptor execution.  
   * @param adaptor - adaptor to be executed
   */
  void execute(AdaptorPluginInterface adaptor);

  /**
   * Clear all scheduled adaptors.
   */
  void clear();
  
  /**
   * Adds an adaptor listener to all adaptors. This listener will be notified whenever one of the adaptors fetches some data. 
   * @param aListener - adaptor listener 
   */
  void addAdaptorListener(AdaptorListenerInterface aListener);
  
  /**
   * Adds an adaptor listener a specific adaptor. This listener will be notified whenever the adaptor fetches some data. 
   * @param aListener - adaptor listener 
   * @param adaptor - adaptor to which the listener should be added
   */
  void addAdaptorListener(AdaptorListenerInterface aListener, AdaptorPluginInterface adaptor);
  
}
