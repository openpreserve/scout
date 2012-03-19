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
public interface NotificationAdaptorInterface {

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
   * @return <code>true</code> if notification should be consumed, i.e. should
   *         not be relayed to any other adaptor
   */
  boolean send(Notification notification);

}
