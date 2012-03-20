package eu.scape_project.watch.components.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.scape_project.watch.components.interfaces.NotificationAdaptorInterface;
import eu.scape_project.watch.core.model.Notification;
import eu.scape_project.watch.core.plugin.PluginException;
import eu.scape_project.watch.core.plugin.PluginType;

/**
 * 
 * Notification adaptor to be used in tests.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class TestNotificationAdaptor implements NotificationAdaptorInterface {

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
  private final List<Notification> notifications = new ArrayList<Notification>();

  /**
   * Whereas this adaptor should consume the events or not.
   */
  private boolean consumeEvent = false;

  @Override
  public Set<String> getSupportedTypes() {
    return TYPES;
  }

  @Override
  public boolean send(final Notification notification) {
    this.notifications.add(notification);
    return this.consumeEvent;
  }

  public List<Notification> getNotifications() {
    return this.notifications;
  }

  public boolean isConsumeEvent() {
    return this.consumeEvent;
  }

  public void setConsumeEvent(final boolean consumeEvent) {
    this.consumeEvent = consumeEvent;
  }

  @Override
  public void init() throws PluginException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void shutdown() throws PluginException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getVersion() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PluginType getPluginType() {
    // TODO Auto-generated method stub
    return null;
  }

}
