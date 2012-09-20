package eu.scape_project.watch.interfaces;

import java.util.Map;
import java.util.Set;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.utils.ConfigParameter;

/**
 * 
 * Definition of Notification adaptor methods.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public interface NotificationPluginInterface extends PluginInterface {

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
   * Use of this method is discouraged as there is a better support
   * for this via the {@link PluginInterface#getParameters()} method.
   * @see {@link ConfigParameter}
   * 
   * @return A map with possible parameter names as key and the data type as
   *         value.
   */
  @Deprecated
  Map<String, DataType> getParametersInfo();

  /**
   * Send a notification.
   * 
   * @param notification
   *          The notification to send
   * @param question
   *          The question that initiated the notification, or null if it reason
   *          to send the notification wasn't based on a question assessment.
   * @param plan
   *          The plan that initiated the notification, or null if it reason to
   *          send the notification wasn't based on a plan assessment
   * @return <code>true</code> if notification should be consumed, i.e. should
   *         not be relayed to any other adaptor
   */
  boolean send(Notification notification, Question question, Plan plan);

}
