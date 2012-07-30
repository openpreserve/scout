package eu.scape_project.watch.interfaces;

public interface SchedulerListenerInterface {

  
  void adaptorPluginWasStarted(AdaptorPluginInterface adaptor);
  
  void adaptorPluginWasStoped(AdaptorPluginInterface adaptor);
  
  void adaptorPluginWasResumed(AdaptorPluginInterface adaptor);
  
  void adaptorPluginWasRescheduled(AdaptorPluginInterface adaptor);
  
  void adaptorPluginWasDeleted(AdaptorPluginInterface adaptor);
  
}
