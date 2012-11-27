package eu.scape_project.watch.notification.email;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * Testing email notifications.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EmailNotificationTest {

  private final Logger log = LoggerFactory.getLogger(EmailNotificationTest.class);

  /**
   * Test if notification sends the email, using a javamail mock.
   * 
   * @throws PluginException
   *           Unexpected plugin exception.
   * @throws MessagingException
   *           Unexpected mock-javamail exception.
   */
  @Test
  public void testSending() throws PluginException, MessagingException {
    final EmailNotification emailNotification = new EmailNotification();

    final String recipientUsername = "test";
    final String recipientDomain = "scoutmockmailserver.com";
    final String recipient = recipientUsername + "@" + recipientDomain;
    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(EmailNotification.PARAM_RECIPIENT, recipient);

    final Notification notification = new Notification("email", parameters);

    final EntityType type = new EntityType("tests", "Test entities");
    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");
    final String sparql = "?s watch:entity watch-Entity:" + entity.getName() + ". ?s watch:property watch-Property:"
      + Property.createId(type.getName(), property.getName() + ". FILTER(?s < 200)");
    final RequestTarget target = RequestTarget.PROPERTY_VALUE;
    final List<EntityType> types = Arrays.asList(type);
    final List<Property> properties = Arrays.asList(property);
    final List<Entity> entities = Arrays.asList(entity);
    final long period = 30000;

    final Question question = new Question(sparql, target);
    final Plan plan = new Plan("plan123");

    final Trigger trigger = new Trigger(types, properties, entities, period, question, plan,
      Arrays.asList(notification));

    final AsyncRequest request = new AsyncRequest("Request description", Arrays.asList(trigger));

    // Sending notification
    emailNotification.init();
    emailNotification.send(notification, trigger, request);
    emailNotification.shutdown();

    // Checking if notification was sent
    final Properties props = System.getProperties();
    props.setProperty("mail.store.protocol", "imaps");
    props.setProperty("mail.imap.partialfetch", "0");

    final Session session = Session.getDefaultInstance(props, null);
    final Store store = session.getStore("imap");
    store.connect(recipientDomain, recipientUsername, "somepassword");

    Folder folder = store.getDefaultFolder();
    folder = folder.getFolder("inbox");
    folder.open(Folder.READ_ONLY);

    final Message[] messages = folder.getMessages();

    Assert.assertEquals(1, messages.length);

    final Message message = messages[0];

    // subject is not blank
    final String subject = message.getSubject();
    Assert.assertNotNull(subject);
    Assert.assertFalse(subject.equals(""));

    log.debug("Message subject: {}", subject);

    // recipient is the same as the one sent
    final Address[] recipients = message.getRecipients(RecipientType.TO);
    Assert.assertEquals(1, recipients.length);

    final Address recipient2 = recipients[0];
    Assert.assertEquals(recipient, recipient2.toString());

    log.debug("Message recipient: {}", recipient);

  }

  /**
   * Cleaning up mailboxes.
   */
  @After
  public void tearDown() {
    Mailbox.clearAll();
  }

}
