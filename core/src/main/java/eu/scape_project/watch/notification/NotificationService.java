package eu.scape_project.watch.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.interfaces.NotificationAdaptorInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Singleton that allows sending of notifications.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class NotificationService {

  /**
   * The singleton instance.
   */
  private static NotificationService instance = null;

  /**
   * Get the singleton instance.
   * 
   * @return The existing instance if exists or a creates a new one.
   */
  public static synchronized NotificationService getInstance() {
    if (instance == null) {
      instance = new NotificationService();
    }
    return instance;
  }

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  /**
   * Registered adaptors.
   */
  private final Set<NotificationAdaptorInterface> adaptors;

  /**
   * Index of adaptors by type.
   */
  private final Map<String, Set<NotificationAdaptorInterface>> adaptorsIndex;

  /**
   * Private constructor for the singleton.
   */
  private NotificationService() {
    this.adaptors = new HashSet<NotificationAdaptorInterface>();
    this.adaptorsIndex = new HashMap<String, Set<NotificationAdaptorInterface>>();

    // TODO load only notification via plugins
    addAdaptor(new LogNotificationAdaptor());
  }

  /**
   * Add a new notification adaptor and update index.
   * 
   * @param adaptor
   *          The notification adaptor to add.
   * @return <code>true</code> if did not already contain the specified adaptor
   */
  public boolean addAdaptor(final NotificationAdaptorInterface adaptor) {
    final boolean ret = this.adaptors.add(adaptor);

    // update index
    for (final String type : adaptor.getSupportedTypes()) {
      Set<NotificationAdaptorInterface> typeAdaptors = this.adaptorsIndex.get(type);

      // add type if this is the first adaptor to support it
      if (typeAdaptors == null) {
        typeAdaptors = new HashSet<NotificationAdaptorInterface>();
        this.adaptorsIndex.put(type, typeAdaptors);
      }

      typeAdaptors.add(adaptor);
    }

    LOG.debug("Registered " + adaptor);

    return ret;
  }

  /**
   * Remove an existing adaptor, updating index.
   * 
   * @param adaptor
   *          The adaptor to remove
   * @return <code>true</code> if contained the specified adaptor
   */
  public boolean removeAdaptor(final NotificationAdaptorInterface adaptor) {
    boolean ret;
    if (adaptor != null) {
      ret = this.adaptors.remove(adaptor);

      // update index
      for (final String type : adaptor.getSupportedTypes()) {
        final Set<NotificationAdaptorInterface> typeAdaptors = this.adaptorsIndex.get(type);

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

  public Set<NotificationAdaptorInterface> getAdaptors() {
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

    final Set<NotificationAdaptorInterface> typeAdaptors = this.adaptorsIndex.get(type);
    for (final NotificationAdaptorInterface adaptor : typeAdaptors) {
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
   * @return <code>true</code> if notification sent by one or more adaptors
   */
  public boolean send(final Notification notification) {
    boolean ret;
    final String type = notification.getType();

    final Set<NotificationAdaptorInterface> typeAdaptors = this.adaptorsIndex.get(type);
    ret = !typeAdaptors.isEmpty();

    for (NotificationAdaptorInterface adaptor : typeAdaptors) {
      final boolean consume = adaptor.send(notification);

      if (consume) {
        break;
      }
    }

    return ret;
  }

}
