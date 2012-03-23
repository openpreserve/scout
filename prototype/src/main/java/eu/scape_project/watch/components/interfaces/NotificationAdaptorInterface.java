package eu.scape_project.watch.components.interfaces;

import eu.scape_project.watch.core.model.DataType;
import eu.scape_project.watch.core.model.Notification;
import eu.scape_project.watch.core.plugin.PluginInterface;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Definition of Notification adaptor methods.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public interface NotificationAdaptorInterface extends PluginInterface {

  /**
   * Get Notification types this adaptors supports.
   * 
   * @return A set of types.
   */
  Set<String> getSupportedTypes();

  /**
   * Get list of parameters and their type.
   * 
   * TODO add parameter description, mandatory/optional flag, readable/password
   * flag, etc.
   * 
   * @return A map with possible parameter names as key and the data type as
   *         value.
   */
  Map<String, DataType> getParametersInfo();

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
