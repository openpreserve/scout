package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import eu.scape_project.watch.core.KBUtils;

/**
 * Possible types of {@link Notification}.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@XmlType(name = KBUtils.NOTIFICATION_TYPE)
@XmlEnum
public enum NotificationType {
  /**
   * Send the notification by email.
   */
  EMAIL_EVENT,
  /**
   * Push the notification to a REST API.
   */
  PUSH_EVENT;
}
