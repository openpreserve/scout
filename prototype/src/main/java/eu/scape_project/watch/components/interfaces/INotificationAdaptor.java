package eu.scape_project.watch.components.interfaces;

import java.util.Set;

import eu.scape_project.watch.core.model.Notification;

/**
 * 
 * Definition of Notification adaptor methods.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public interface INotificationAdaptor {

  /**
   * Get Notification types this adaptors supports.
   * 
   * @return A set of types.
   */
  Set<String> getSupportedTypes();

  /**
   * Send a notification.
   * 
   * @param notification
   *          The notification to send
   */
  void send(Notification notification);

}
