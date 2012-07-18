package eu.scape_project.watch.notification.email.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Utility methods for sending emails.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class MailUtils {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(MailUtils.class);

  /**
   * Utility classes should have private constructor.
   */
  private MailUtils() {

  }

  /**
   * Send an HTML email.
   * 
   * @param recipients
   *          The list of email recipients where the email should be sent to.
   * @param subject
   *          The email subject.
   * @param message
   *          The HTML message.
   * @param fromAddress
   *          The sender email address that should be set on the email.
   * @param fromName
   *          The sender name that should be set on the email.
   * @throws MessagingException
   *           Any error that might occur on sending the message.
   */
  public static void sendEmail(final String[] recipients, final String subject, final String message,
    final String fromAddress, final String fromName) throws MessagingException {
    final boolean debug = false;

    // Set the host smtp address
    final Properties props = new Properties();
    try {
      props.load(MailUtils.class.getResourceAsStream("/mail.properties"));
    } catch (final IOException e) {
      LOG.error("Could not find mail.properties, using localhost as SMTP host", e);
      props.put("mail.smtp.host", "localhost");
    }

    // create some properties and get the default Session
    final Session session = Session.getDefaultInstance(props, null);
    session.setDebug(debug);

    // create a message
    final Message msg = new MimeMessage(session);

    // set the from and to address
    InternetAddress addressFrom;
    try {
      addressFrom = new InternetAddress(fromAddress, fromName);
    } catch (final UnsupportedEncodingException e) {
      addressFrom = new InternetAddress(fromAddress);
    }
    msg.setFrom(addressFrom);

    final InternetAddress[] addressTo = new InternetAddress[recipients.length];
    for (int i = 0; i < recipients.length; i++) {
      addressTo[i] = new InternetAddress(recipients[i]);
    }
    msg.setRecipients(Message.RecipientType.TO, addressTo);

    // Optional: You can also set your custom headers
    // msg.addHeader("MyHeaderName", "myHeaderValue");

    // Setting the Subject and Content Type
    msg.setSubject(subject);
    msg.setContent(message, "text/html; charset=\"UTF-8\"");
    Transport.send(msg);
  }
}
