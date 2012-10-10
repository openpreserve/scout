package eu.scape_project.watch.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import eu.scape_project.watch.utils.KBUtils;

/**
 * Possible event types of {@link SourceAdaptorEvent}.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@XmlType(name = KBUtils.SOURCE_ADAPTOR_EVENT_TYPE)
@XmlEnum
public enum SourceAdaptorEventType {
  /**
   * Automatically/manually started up the adaptor.
   */
  STARTED,
  /**
   * Manually stopped/paused the adaptor.
   */
  STOPPED,
  /**
   * Resumed a stopped adaptor.
   */
  RESUMED,
  /**
   * Running the adaptor (immediatly) again because of some failure.
   */
  RESCHEDULED,
  /**
   * Deactivated the adaptor because of a permanent failure.
   */
  DELETED,
  /**
   * Ran the adaptor.
   */
  EXECUTED;
}
