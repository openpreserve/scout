package eu.scape_project.watch.notification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Entry;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.interfaces.NotificationAdaptorInterface;
import eu.scape_project.watch.plugin.PluginException;
import eu.scape_project.watch.plugin.PluginType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy adaptor that simply writes every notification to log.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class LogNotificationAdaptor implements NotificationAdaptorInterface {

  /**
   * The notification plugin name.
   */
  private static final String NAME = LogNotificationAdaptor.class.getSimpleName();

  /**
   * The notification plugin version.
   */
  private static final String VERSION = "0.0.1";

  /**
   * Message parameter key.
   */
  public static final String PARAM_MESSAGE = "message";

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
  public boolean send(final Notification notification) {
    final StringBuilder message = new StringBuilder();

    message.append("NOTIFICATION LOG ");
    message.append("{");
    boolean first = true;
    for (final Entry entry : notification.getParameters()) {
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
    ret.put(PARAM_MESSAGE, DataType.TEXT);
    return ret;
  }

}
