package eu.scape_project.watch.interfaces;

public interface SchedulerListenerInterface {

  
  void adaptorPluginWasStarted(AdaptorPluginInterface adaptor, EventDetails details);
  
  void adaptorPluginWasStoped(AdaptorPluginInterface adaptor, EventDetails details);
  
  void adaptorPluginWasResumed(AdaptorPluginInterface adaptor, EventDetails details);
  
  void adaptorPluginWasRescheduled(AdaptorPluginInterface adaptor, EventDetails details);
  
  void adaptorPluginWasDeleted(AdaptorPluginInterface adaptor, EventDetails details);
  
  void adaptorPluginWasExecuted(AdaptorPluginInterface adaptor, EventDetails details);
  
}
