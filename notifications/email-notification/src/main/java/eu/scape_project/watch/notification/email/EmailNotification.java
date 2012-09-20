package eu.scape_project.watch.notification.email;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.interfaces.NotificationPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.notification.email.utils.MailUtils;
import eu.scape_project.watch.utils.ConfigParameter;
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
  private static final String NAME = "HtmlEmail";

  /**
   * Plug-in version.
   */
  private static final String VERSION = "0.0.2";

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
  
  private static final List<ConfigParameter> PARAMETERS = new ArrayList<ConfigParameter>();

  /**
   * Recipients parameter key.
   */
  public static final String PARAM_RECIPIENT = "recipient";
  
  public static final String PARAM_RECIPIENT_DESC = "The email of the recipient of this notification.";

  static {
    final ConfigParameter cp = new ConfigParameter(PARAM_RECIPIENT, null, PARAM_RECIPIENT_DESC, true, false);
    PARAMETERS_INFO.put(PARAM_RECIPIENT, DataType.STRING);
    PARAMETERS.add(cp);
  }

  /**
   * Logger.
   */
  private final Logger log = LoggerFactory.getLogger(EmailNotification.class);

  /**
   * The email subject.
   */
  private String subject;

  /**
   * The email from address.
   */
  private String fromAddress;

  /**
   * The email from name.
   */
  private String fromName;

  /**
   * The compiled mustache template.
   */
  private Mustache mustache;

  @Override
  public void init() throws PluginException {
    final ResourceBundle bundle = ResourceBundle.getBundle("email_notification");
    subject = bundle.getString("subject");
    fromAddress = bundle.getString("fromAddress");
    fromName = bundle.getString("fromName");
    
    final String template = bundle.getString("template");
    final MustacheFactory mf = new DefaultMustacheFactory();
    mustache = mf.compile(template);
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
  public List<ConfigParameter> getParameters() {
    return PARAMETERS;
  }

  @Override
  public boolean send(final Notification notification, final Question question, final Plan plan) {

    final Map<String, String> parameters = notification.getParameterMap();
    final String recipient = parameters.get(PARAM_RECIPIENT);

    final String[] recipients = new String[] {recipient};

    final Map<String, Object> messageParams = new HashMap<String, Object>();
    
    messageParams.put("subject", subject);
    // TODO add an 'archive' parameter with a link to the archived copy of the email
    // TODO add an 'description' parameter with the description of the trigger
    
 
   // TODO add an 'twitter' parameter with a link to a twitter profile
   // TODO add an 'facebook' parameter with a link to a facebook profile
   // TODO add an 'forward' parameter with a link that would forward this email
    // XXX un-comment social section in mustache if any of the above are activated
    
    if (question != null) {
      messageParams.put("question", question);
    }
    if (plan != null) {
      messageParams.put("plan", plan);
    }

    final ByteArrayOutputStream msgByteArray = new ByteArrayOutputStream();
    final Writer msgWriter = new OutputStreamWriter(msgByteArray);
    mustache.execute(msgWriter, messageParams);

    try {
      msgWriter.flush();

      final String message = msgByteArray.toString();

      log.trace("Sending message to {} with content:\n'{}'", recipient, message);
      MailUtils.sendEmail(recipients, subject, message, fromAddress, fromName);
      return true;
    } catch (final MessagingException e) {
      log.error("Error sending email notification", e);
      return false;
    } catch (IOException e) {
      log.error("Error sending email notification", e);
      return false;
    }

  }

  /**
   * Real test.
   * 
   * @param args
   *          The recipient email in args[0].
   */
  public static void main(final String[] args) {

    if (args.length != 1) {
      System.err.println("Syntax: EmailNotification recipient@example.com");
      System.exit(1);
    }

    final EmailNotification email = new EmailNotification();
    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(PARAM_RECIPIENT, args[0]);

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

    final Question question = new Question(sparql, target, types, properties, entities, period);

    final Plan plan = new Plan("plan123");

    try {
      email.init();
      email.send(notification, question, plan);
      email.shutdown();
    } catch (final PluginException e) {
      e.printStackTrace();
      System.exit(2);
    }
  }


}
