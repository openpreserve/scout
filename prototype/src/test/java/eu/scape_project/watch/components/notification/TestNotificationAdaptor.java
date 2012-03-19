package eu.scape_project.watch.components.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.scape_project.watch.components.interfaces.INotificationAdaptor;
import eu.scape_project.watch.core.model.Notification;

/**
 * 
 * Notification adaptor to be used in tests.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class TestNotificationAdaptor implements INotificationAdaptor {

  /**
   * Test notification type.
   */
  public static final String TEST_TYPE = "test";

  /**
   * Supported types.
   */
  private static final Set<String> TYPES = new HashSet<String>(Arrays.asList(TEST_TYPE));

  /**
   * List of sent notifications.
   */
  private static final List<Notification> NOTIFICATIONS = new ArrayList<Notification>();

  @Override
  public Set<String> getSupportedTypes() {
    return TYPES;
  }

  @Override
  public void send(final Notification notification) {
    NOTIFICATIONS.add(notification);
  }

  public static List<Notification> getNotifications() {
    return NOTIFICATIONS;
  }

}
