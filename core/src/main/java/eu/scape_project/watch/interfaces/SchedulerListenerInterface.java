package eu.scape_project.watch.interfaces;

import eu.scape_project.watch.domain.SourceAdaptorEvent;

public interface SchedulerListenerInterface {

  void onEvent(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

}
