package eu.scape_project.watch.interfaces;

import eu.scape_project.watch.domain.SourceAdaptorEvent;

public interface SchedulerListenerInterface {

  void adaptorPluginWasStarted(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  void adaptorPluginWasStopped(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  void adaptorPluginWasResumed(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  void adaptorPluginWasRescheduled(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  void adaptorPluginWasDeleted(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

  void adaptorPluginWasExecuted(AdaptorPluginInterface adaptor, SourceAdaptorEvent event);

}
