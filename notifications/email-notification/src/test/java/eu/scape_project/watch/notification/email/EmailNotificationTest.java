package eu.scape_project.watch.notification.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * Testing email notifications.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EmailNotificationTest {

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

    // Sending notification
    emailNotification.init();
    emailNotification.send(notification);
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
  }

  /**
   * Cleaning up mailboxes.
   */
  @After
  public void tearDown() {
    Mailbox.clearAll();
  }

}
