package eu.scape_project.watch.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.NotificationPluginInterface;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * 
 * Singleton that allows sending of notifications.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class NotificationService {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  /**
   * Registered adaptors.
   */
  private final Set<NotificationPluginInterface> adaptors;

  /**
   * Index of adaptors by type.
   */
  private final Map<String, Set<NotificationPluginInterface>> adaptorsIndex;

  /**
   * New notification service.
   */
  public NotificationService() {
    this.adaptors = new HashSet<NotificationPluginInterface>();
    this.adaptorsIndex = new HashMap<String, Set<NotificationPluginInterface>>();

    // load only notification via plugins
    // addAdaptor(new LogNotificationAdaptor());
  }

  /**
   * Add a new notification adaptor and update index.
   * 
   * @param adaptor
   *          The notification adaptor to add.
   * @return <code>true</code> if did not already contain the specified adaptor
   */
  public boolean addAdaptor(final NotificationPluginInterface adaptor) {
    boolean ret;
    try {
      adaptor.init();
      ret = this.adaptors.add(adaptor);

      // update index
      for (final String type : adaptor.getSupportedTypes()) {
        Set<NotificationPluginInterface> typeAdaptors = this.adaptorsIndex.get(type);

        // add type if this is the first adaptor to support it
        if (typeAdaptors == null) {
          typeAdaptors = new HashSet<NotificationPluginInterface>();
          this.adaptorsIndex.put(type, typeAdaptors);
        }

        typeAdaptors.add(adaptor);
      }

      LOG.debug("Registered " + adaptor);
    } catch (PluginException e) {
      ret = false;
      LOG.error("Could not load notification plugin", e);
    }

    return ret;
  }

  /**
   * Remove an existing adaptor, updating index.
   * 
   * @param adaptor
   *          The adaptor to remove
   * @return <code>true</code> if contained the specified adaptor
   */
  public boolean removeAdaptor(final NotificationPluginInterface adaptor) {
    boolean ret;
    if (adaptor != null) {
      ret = this.adaptors.remove(adaptor);

      // update index
      for (final String type : adaptor.getSupportedTypes()) {
        final Set<NotificationPluginInterface> typeAdaptors = this.adaptorsIndex.get(type);

        if (typeAdaptors != null) {
          typeAdaptors.remove(adaptor);

          // Remove type if no adaptor supports it
          if (typeAdaptors.isEmpty()) {
            this.adaptorsIndex.remove(type);
          }
        }
      }

      LOG.debug("Unregistered " + adaptor);
    } else {
      ret = false;
    }

    return ret;
  }

  public Set<NotificationPluginInterface> getAdaptors() {
    return adaptors;
  }

  /**
   * Get the set of available notification types from all installed adapters.
   * 
   * @return A set of notification types.
   */
  public Set<String> getTypes() {
    return this.adaptorsIndex.keySet();
  }

  /**
   * Get all possible parameters for a notification type.
   * 
   * @param type
   *          The notification type.
   * @return A map will all the parameters.
   */
  public Map<String, DataType> getTypeParameters(final String type) {
    final Map<String, DataType> ret = new HashMap<String, DataType>();

    final Set<NotificationPluginInterface> typeAdaptors = this.adaptorsIndex.get(type);
    for (final NotificationPluginInterface adaptor : typeAdaptors) {
      final Map<String, DataType> adaptorParam = adaptor.getParametersInfo();
      ret.putAll(adaptorParam);
    }

    return ret;
  }

  /**
   * Send a notification by all adaptors that support the notification type.
   * 
   * @param notification
   *          The notification to send
   * @param question
   *          The question that initiated the notification, or null if it reason
   *          to send the notification wasn't based on a question assessment.
   * @param plan
   *          The plan that initiated the notification, or null if it reason to
   *          send the notification wasn't based on a plan assessment
   * @return <code>true</code> if notification sent by one or more adaptors
   * 
   * @see NotificationPluginInterface#send(Notification, Question, Plan)
   */
  public boolean send(final Notification notification, final Trigger trigger, final AsyncRequest request) {
    boolean ret;
    final String type = notification.getType();

    final Set<NotificationPluginInterface> typeAdaptors = this.adaptorsIndex.get(type);
    ret = typeAdaptors != null && !typeAdaptors.isEmpty();

    if (typeAdaptors != null) {
      for (NotificationPluginInterface adaptor : typeAdaptors) {
        final boolean consume = adaptor.send(notification, trigger, request);

        if (consume) {
          break;
        }
      }
    }

    if (!ret) {
      LOG.warn("Could not find any notification plugins to send {}", notification);
    }

    return ret;
  }

}
