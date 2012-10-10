package eu.scape_project.watch.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
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

  private void registerEvent(AdaptorPluginInterface adaptorInterface, SourceAdaptorEvent event) {
    final SourceAdaptor sourceAdaptor = adaptorManager.getSourceAdaptor(adaptorInterface);

    if (sourceAdaptor != null) {
      event.setAdaptor(sourceAdaptor);
      DAO.save(event);
    } else {
      log.warn("Could not save adaptor event because adaptor was not found");
    }
  }

  @Override
  public void onEvent(AdaptorPluginInterface adaptor, SourceAdaptorEvent event) {
    registerEvent(adaptor, event);
  }

}
