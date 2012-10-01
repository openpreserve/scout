package eu.scape_project.watch.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.EventDetails;
import eu.scape_project.watch.interfaces.SchedulerListenerInterface;

/**
 * Registers the adaptor life-cycle events on the knowledge base.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class AdaptorsLifecycleListener implements SchedulerListenerInterface {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final AdaptorManager adaptorManager;

  public AdaptorsLifecycleListener(AdaptorManager adaptorManager) {
    super();
    this.adaptorManager = adaptorManager;
  }

  private SourceAdaptorEvent createEvent(AdaptorPluginInterface adaptorInterface, EventDetails details) {
    SourceAdaptorEvent event = null;
    final SourceAdaptor sourceAdaptor = adaptorManager.getSourceAdaptor(adaptorInterface);

    if (sourceAdaptor != null) {
      event = new SourceAdaptorEvent(details.getMessage(), details.isSucessful(), details.getReason(), new Date(),
        sourceAdaptor);
    }
    return event;
  }

  private void registerEvent(AdaptorPluginInterface adaptorInterface, EventDetails details) {
    final SourceAdaptorEvent event = createEvent(adaptorInterface, details);

    if (event != null) {
      DAO.save(event);
    } else {
      log.warn("Could not save adaptor event!");
    }
  }

  @Override
  public void adaptorPluginWasStarted(AdaptorPluginInterface adaptor, EventDetails details) {
    registerEvent(adaptor, details);
  }

  @Override
  public void adaptorPluginWasStoped(AdaptorPluginInterface adaptor, EventDetails details) {
    registerEvent(adaptor, details);
  }

  @Override
  public void adaptorPluginWasResumed(AdaptorPluginInterface adaptor, EventDetails details) {
    registerEvent(adaptor, details);
  }

  @Override
  public void adaptorPluginWasRescheduled(AdaptorPluginInterface adaptor, EventDetails details) {
    registerEvent(adaptor, details);
  }

  @Override
  public void adaptorPluginWasDeleted(AdaptorPluginInterface adaptor, EventDetails details) {
    // If it was deleted it cannot be created!
    // registerEvent(adaptor, details);
  }

  @Override
  public void adaptorPluginWasExecuted(AdaptorPluginInterface adaptor, EventDetails details) {
    registerEvent(adaptor, details);
  }

}
