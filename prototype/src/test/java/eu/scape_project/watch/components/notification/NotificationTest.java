package eu.scape_project.watch.components.notification;

import eu.scape_project.watch.components.interfaces.INotificationAdaptor;
import eu.scape_project.watch.core.model.Notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Test Notification Service.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class NotificationTest {

  /**
   * The test notification adaptor used in all tests.
   */
  private static TestNotificationAdaptor adaptor;

  /**
   * The notification service instance.
   */
  private static NotificationService service = NotificationService.getInstance();

  /**
   * Initialized test adaptor and service.
   * 
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    adaptor = new TestNotificationAdaptor();
    service.addAdaptor(adaptor);
  }

  /**
   * Remove test adaptor from service.
   */
  @AfterClass
  public static void tearDownAfterClass() {
    service.removeAdaptor(adaptor);
  }

  /**
   * Test the list of adaptors.
   */
  @Test
  public void testGetAdaptors() {
    final Set<INotificationAdaptor> adaptors = service.getAdaptors();
    Assert.assertTrue(adaptors.contains(adaptor));
  }

  /**
   * Test the list of types.
   */
  @Test
  public void testGetTypes() {
    final Set<String> serviceTypes = service.getTypes();
    final Set<String> adaptorTypes = adaptor.getSupportedTypes();
    Assert.assertTrue(serviceTypes.containsAll(adaptorTypes));
  }

  /**
   * Test the sending of a notification.
   */
  @Test
  public void testSend() {
    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("test", "test01");
    final Notification notification = new Notification(TestNotificationAdaptor.TEST_TYPE, parameters);
    service.send(notification);

    final List<Notification> sentNotifications = TestNotificationAdaptor.getNotifications();
    Assert.assertTrue(sentNotifications.contains(notification));

  }

}
