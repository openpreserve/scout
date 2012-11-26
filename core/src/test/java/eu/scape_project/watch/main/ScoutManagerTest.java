package eu.scape_project.watch.main;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.notification.DummyNotificationAdaptor;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.plugin.PluginManagerTest;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

public class ScoutManagerTest {

  /**
   * A temporary directory to hold the data.
   */
  private static final String DATA_TEMP_DIR = "/tmp/watch";

  /**
   * Initialize the data folder.
   */
  @Before
  public void beforeClass() {
    final String datafolder = DATA_TEMP_DIR;
    final boolean initdata = false;
    KBUtils.dbConnect(datafolder, initdata);
  }

  /**
   * Cleanup the data folder.
   */
  @After
  public void afterClass() {
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(new File(DATA_TEMP_DIR));
  }

  @Test
  public void lifecycleByEventsTest() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    final ScoutManager scout = new ScoutManager();
    final DummyNotificationAdaptor dummyNotificationAdaptor = new DummyNotificationAdaptor();

    scout.start();
    scout.getNotificationService().addAdaptor(dummyNotificationAdaptor);
    PluginManagerTest.copyTestJar(PluginManagerTest.ADAPTOR_1);
    PluginManager.getDefaultPluginManager().getConfig().override(ConfigUtils.DEFAULT_CONFIG);
    PluginManager.getDefaultPluginManager().setup();

    final EntityType type = new EntityType("formats", "file formats");
    final Property property = new Property(type, "mime", "Mime type");
    final Entity entity = new Entity(type, "image/jpeg");
    final Source source = new Source("test", "Test data");
    final SourceAdaptor adaptor = new SourceAdaptor(PluginManagerTest.ADAPTOR_1_NAME,
        PluginManagerTest.ADAPTOR_1_VERSION, "default", source, Arrays.asList(type), Arrays.asList(property),
        new HashMap<String, String>());

    final Question question = new Question("", RequestTarget.PROPERTY_VALUE, Arrays.asList(type), null, null, 0);
    final Notification notification = new Notification(DummyNotificationAdaptor.TEST_TYPE,
        new HashMap<String, String>());
    final Trigger trigger = new Trigger(question, Arrays.asList(notification), null);
    final AsyncRequest request = new AsyncRequest("Test", Arrays.asList(trigger));

    // Save data model
    DAO.save(type);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);

    // Install request
    DAO.save(request);

    // Save data and trigger request
    DAO.save(entity);
    DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, "123"));

    Assert.assertTrue(dummyNotificationAdaptor.getNotifications().contains(notification));
    Assert.assertEquals(1, dummyNotificationAdaptor.getNotifications().size());

    scout.stop();
  }

  @Test
  public void lifecycleBySchedulingTest() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    final ScoutManager scout = new ScoutManager();
    final DummyNotificationAdaptor dummyNotificationAdaptor = new DummyNotificationAdaptor();

    scout.start();
    scout.getNotificationService().addAdaptor(dummyNotificationAdaptor);
    PluginManagerTest.copyTestJar(PluginManagerTest.ADAPTOR_1);
    PluginManager.getDefaultPluginManager().getConfig().override(ConfigUtils.DEFAULT_CONFIG);
    PluginManager.getDefaultPluginManager().setup();

    final EntityType type = new EntityType("formats", "file formats");
    final Property property = new Property(type, "mime", "Mime type");
    final Entity entity = new Entity(type, "image/jpeg");
    final Source source = new Source("test", "Test data");
    final SourceAdaptor adaptor = new SourceAdaptor(PluginManagerTest.ADAPTOR_1_NAME,
        PluginManagerTest.ADAPTOR_1_VERSION, "default", source, Arrays.asList(type), Arrays.asList(property),
        new HashMap<String, String>());

    final Question question = new Question("", RequestTarget.PROPERTY_VALUE, null, null, null, 3000);
    final Notification notification = new Notification(DummyNotificationAdaptor.TEST_TYPE,
        new HashMap<String, String>());
    final Trigger trigger = new Trigger(question, Arrays.asList(notification), null);
    final AsyncRequest request = new AsyncRequest("Test", Arrays.asList(trigger));

    // Save data model
    DAO.save(type);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);

    // Install request
    DAO.save(request);

    // Save data and trigger request
    DAO.save(entity);
    DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, "123"));

    // Waiting for scheduling period to finish
    try {
      Thread.sleep(5000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Assert.assertTrue(dummyNotificationAdaptor.getNotifications().contains(notification));
    Assert.assertEquals(1, dummyNotificationAdaptor.getNotifications().size());

    scout.stop();
  }
}
