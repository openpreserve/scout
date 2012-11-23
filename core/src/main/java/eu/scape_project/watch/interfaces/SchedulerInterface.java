package eu.scape_project.watch.interfaces;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.SourceAdaptorEvent;

/**
 * This interface is responsible for providing methods for scheduling adaptors.
 * 
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
   * Method which is responsible to start an adaptor. To find out which
   * parameters are needed check specific implementations of this interface.
   * 
   * @param adaptor
   *          - AdaptorPluginInterface to be started
   * @param event
   *          - Details which will be passed to SchedulerListeners
   */
  void startAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  /**
   * Method responsible to stop an already running adaptor. Once paused adaptor
   * can be only started again with resume method.
   * 
   * @param adptor
   *          - adaptor to be stopped
   * @param details
   *          - Details which will be passed to SchedulerListeners
   */
  void stopAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  /**
   * Method responsible for resuming stopped adaptor.
   * 
   * @param adptor
   *          - adaptor to be resumed
   * @param details
   *          - Details which will be passed to SchedulerListeners
   */
  void resumeAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  /**
   * Method for deleting specific adaptor.
   * 
   * @param adaptor
   *          - adaptor to be deleted
   * @param details
   *          - Details which will be passed to SchedulerListeners
   */
  void deleteAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  /**
   * Method which allows immediate adaptor execution.
   * 
   * @param adaptor
   *          - adaptor to be executed
   * @param details
   *          - Details which will be passed to SchedulerListeners
   */
  void executeAdaptor(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  void startRequest(AsyncRequest request);

  void stopRequest(AsyncRequest request);

  /**
   * Adds an adaptor listener to all adaptors. This listener will be notified
   * whenever one of the adaptors fetches some data.
   * 
   * @param aListener
   *          - adaptor listener
   */
  void addAdaptorListener(AdaptorListenerInterface aListener);

  /**
   * Adds an adaptor listener for a specific adaptor. This listener will be
   * notified whenever the adaptor fetches some data.
   * 
   * @param aListener
   *          - adaptor listener
   * @param adaptor
   *          - adaptor to which the listener should be added
   */
  void addAdaptorListener(AdaptorListenerInterface aListener, AdaptorPluginInterface adaptor);

  /**
   * Removes an adaptor listener.
   * 
   * @param aListener
   *          - adaptor listener to be removed
   */
  void removeAdaptorListener(AdaptorListenerInterface aListener);

  /**
   * Adds a scheduler listener. This listener will be notified when a specific
   * operation (start, stop, ...) happens on a specific adaptor.
   * 
   * @param listener
   *          - listener to be added
   */
  void addSchedulerListener(SchedulerListenerInterface listener);

  /**
   * Removes specific scheduelr listener.
   * 
   * @param listener
   *          - listener to be removed
   */
  void removeSchedulerListener(SchedulerListenerInterface listener);
}
