package eu.scape_project.watch.notification.email;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.interfaces.NotificationPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.notification.email.utils.MailUtils;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * Plug-in to send notifications via email.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EmailNotification implements NotificationPluginInterface {

  /**
   * Plug-in name.
   */
  private static final String NAME = "PlainTextEmail";

  /**
   * Plug-in version.
   */
  private static final String VERSION = "0.0.1-SNAPSHOT";

  /**
   * Plug-in description.
   */
  private static final String DESCRIPTION = "Send notification via email";

  /**
   * List of supported notification types.
   */
  private static final Set<String> SUPPORTED_TYPES = new HashSet<String>(Arrays.asList("email"));

  /**
   * Information about what parameters should be present in the notification.
   */
  private static final Map<String, DataType> PARAMETERS_INFO = new HashMap<String, DataType>();

  /**
   * Recipients parameter key.
   */
  public static final String PARAM_RECIPIENT = "recipient";

  static {
    PARAMETERS_INFO.put(PARAM_RECIPIENT, DataType.STRING);
  }

  /**
   * Logger.
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void init() throws PluginException {
    // nothing to do
  }

  @Override
  public void shutdown() throws PluginException {
    // nothing to do
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getVersion() {
    return VERSION;
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }

  @Override
  public PluginType getPluginType() {
    return PluginType.NOTIFICATION;
  }

  @Override
  public Set<String> getSupportedTypes() {
    return SUPPORTED_TYPES;
  }

  @Override
  public Map<String, DataType> getParametersInfo() {
    return PARAMETERS_INFO;
  }

  @Override
  public boolean send(final Notification notification) {

    final Map<String, String> parameters = notification.getParameterMap();
    final String recipient = parameters.get(PARAM_RECIPIENT);

    final String[] recipients = new String[] {recipient};

    final ResourceBundle bundle = ResourceBundle.getBundle("email_notification");
    final String subject = bundle.getString("subject");
    final String fromAddress = bundle.getString("fromAddress");
    final String fromName = bundle.getString("fromName");
    final String message = bundle.getString("message");

    try {
      MailUtils.sendEmail(recipients, subject, message, fromAddress, fromName);
      return true;
    } catch (final MessagingException e) {
      log.error("Error sending email notification", e);
      return false;
    }

  }

}
