package eu.scape_project.watch.notification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.NotificationPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * Dummy adaptor that simply writes every notification to log.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class LogNotificationAdaptor implements NotificationPluginInterface {

  /**
   * The notification plugin name.
   */
  private static final String NAME = LogNotificationAdaptor.class.getSimpleName();

  /**
   * The notification plugin version.
   */
  private static final String VERSION = "0.0.2";

  /**
   * Message parameter key.
   */
  public static final String PARAM_MESSAGE = "message";

  /**
   * The description of the param message.
   */
  private static final String PARAM_MESSAGE_DESC = "The log statement that will be printed on the appended to the log";

  /**
   * Supported types.
   */
  private static final Set<String> TYPES = new HashSet<String>(Arrays.asList("log"));

  /**
   * Logger.
   */
  private final Logger log = LoggerFactory.getLogger(LogNotificationAdaptor.class);

  @Override
  public Set<String> getSupportedTypes() {
    return TYPES;
  }

  @Override
  public boolean send(final Notification notification, final Trigger trigger, final AsyncRequest request) {
    final StringBuilder message = new StringBuilder();

    message.append("NOTIFICATION LOG ");
    message.append("{");
    boolean first = true;
    for (final DictionaryItem entry : notification.getParameters()) {
      if (first) {
        first = false;
      } else {
        message.append(", ");
      }
      message.append(entry.getKey());
      message.append(": ");
      message.append(entry.getValue());
    }
    message.append("}");

    this.log.info(message.toString());
    return false;
  }

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
    return "Write notifications to the log file";
  }

  @Override
  public PluginType getPluginType() {
    return PluginType.NOTIFICATION;
  }

  @Override
  public Map<String, DataType> getParametersInfo() {
    final Map<String, DataType> ret = new HashMap<String, DataType>();
    ret.put(PARAM_MESSAGE, DataType.STRING);
    return ret;
  }

  @Override
  public List<ConfigParameter> getParameters() {
    final ConfigParameter cp = new ConfigParameter(PARAM_MESSAGE, null, PARAM_MESSAGE_DESC, true, false);
    return Arrays.asList(cp);
  }

}
