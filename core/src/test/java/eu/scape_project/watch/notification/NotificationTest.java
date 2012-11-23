package eu.scape_project.watch.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.interfaces.NotificationPluginInterface;

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
  private static DummyNotificationAdaptor adaptor;

  /**
   * The notification service instance.
   */
  private static NotificationService service = new NotificationService();

  /**
   * Initialized test adaptor and service.
   * 
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    adaptor = new DummyNotificationAdaptor();
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
    final Set<NotificationPluginInterface> adaptors = service.getAdaptors();
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
    final Notification notification = new Notification(DummyNotificationAdaptor.TEST_TYPE, parameters);
    service.send(notification, null, null);

    final List<Notification> sentNotifications = adaptor.getNotifications();
    Assert.assertTrue(sentNotifications.contains(notification));
  }

  /**
   * Test the sending of a notification.
   */
  @Test
  public void testEventConsuming() {

    // set up
    service.removeAdaptor(adaptor);

    final DummyNotificationAdaptor adaptor1 = new DummyNotificationAdaptor();
    final DummyNotificationAdaptor adaptor2 = new DummyNotificationAdaptor();

    service.addAdaptor(adaptor1);
    service.addAdaptor(adaptor2);

    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("test", "test01");
    final Notification notification = new Notification(DummyNotificationAdaptor.TEST_TYPE, parameters);

    // test not consume
    adaptor1.setConsumeEvent(false);
    adaptor2.setConsumeEvent(false);

    service.send(notification, null, null);

    boolean adaptor1Contains = adaptor1.getNotifications().contains(notification);
    boolean adaptor2Contains = adaptor2.getNotifications().contains(notification);
    Assert.assertTrue(adaptor1Contains);
    Assert.assertTrue(adaptor2Contains);

    // clean notifications
    adaptor1.getNotifications().clear();
    adaptor2.getNotifications().clear();

    // test consume
    adaptor1.setConsumeEvent(true);
    adaptor2.setConsumeEvent(true);

    service.send(notification, null, null);
    adaptor1Contains = adaptor1.getNotifications().contains(notification);
    adaptor2Contains = adaptor2.getNotifications().contains(notification);
    Assert.assertFalse(adaptor1Contains && adaptor2Contains);
    Assert.assertTrue(adaptor1Contains || adaptor2Contains);

    // clean up
    service.removeAdaptor(adaptor1);
    service.removeAdaptor(adaptor2);

    service.addAdaptor(adaptor);
  }
}
