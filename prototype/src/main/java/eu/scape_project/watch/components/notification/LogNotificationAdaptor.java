package eu.scape_project.watch.components.notification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import eu.scape_project.watch.components.interfaces.INotificationAdaptor;
import eu.scape_project.watch.core.model.Entry;
import eu.scape_project.watch.core.model.Notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy adaptor that simply writes every notification to log.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class LogNotificationAdaptor implements INotificationAdaptor {

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
  public void send(final Notification notification) {
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
  }

}
