package eu.scape_project.watch.interfaces;

import java.util.Map;

/**
 * This interface is responsible for providing methods for scheduling adaptors. 
 * @author Kresimir Duretec <duretec@ifs.tuwien.ac.at>
 *
 */
public interface SchedulerInterface {

  /**
   * Method for scheduler initialization.  
   */
  void init(); 
  
  /**
   * Method for scheduler shutdown. 
   */
  void shutdown(); 
  
  /**
   * Method which is responsible to start an adaptor. Possible scheduling parameters like execution interval 
   * or similar are passed as an argument. To find out which parameters are needed check specific implementations
   * of this interface.   
   * @param adaptor - AdaptorPluginInterface to be started 
   * @param properties - properties needed to successfully start an adaptor
   * @param details - Details which will be passed to SchedulerListeners 
   */
  void start(AdaptorPluginInterface adaptor, Map<String, String> properties, EventDetails details);

  /**
   * Method responsible to stop an already running adaptor. Once paused adaptor can be only started again with 
   * resume method. 
   * @param adptor -  adaptor to be stopped
   * @param details - Details which will be passed to SchedulerListeners 
   */
  void stop(AdaptorPluginInterface adaptor, EventDetails details); 
  
  /**
   * Method responsible for resuming stopped adaptor. 
   * @param adptor - adaptor to be resumed 
   * @param details - Details which will be passed to SchedulerListeners 
   */
  void resume(AdaptorPluginInterface adaptor, EventDetails details); 
  
  /**
   * Method for deleting specific adaptor. 
   * @param adaptor - adaptor to be deleted 
   * @param details - Details which will be passed to SchedulerListeners 
   */
  void delete(AdaptorPluginInterface adaptor, EventDetails details);
  
  /**
   * Method which allows immediate adaptor execution.  
   * @param adaptor - adaptor to be executed
   * @param details - Details which will be passed to SchedulerListeners 
   */
  void execute(AdaptorPluginInterface adaptor, EventDetails details);

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
   * Adds an adaptor listener for a specific adaptor. This listener will be notified whenever the adaptor fetches some data. 
   * @param aListener - adaptor listener 
   * @param adaptor - adaptor to which the listener should be added
   */
  void addAdaptorListener(AdaptorListenerInterface aListener, AdaptorPluginInterface adaptor);
  
  /**
   * Removes an adaptor listener.
   * @param aListener - adaptor listener to be removed
   */
  void removeAdaptorListener(AdaptorListenerInterface aListener);

  /**
   * Adds a scheduler listener. This listener will be notified when a specific operation (start, stop ..) happens on a specific 
   * adaptor. 
   * @param listener - listener to be added 
   */
  void addSchedulerListener(SchedulerListenerInterface listener);
  
  /**
   * Removes specific scheduelr listener. 
   * @param listener - listener to be removed
   */
  void removeSchedulerListener(SchedulerListenerInterface listener);
}
