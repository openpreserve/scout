package eu.scape_project.watch.scheduling.quartz;

import java.util.List;
import java.util.Map;

import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;

public class QuartzListenerManager {

	private List<AdaptorListenerInterface> listenersAll;
	
	private Map<AdaptorPluginInterface, List<AdaptorListenerInterface>> listeners;
	
	public void notify(AdaptorPluginInterface adaptor, ResultInterface result){
		
		
		for (AdaptorListenerInterface listener : listenersAll) {
			listener.notify(adaptor, result);
		}
		
		for (AdaptorListenerInterface listener : listeners.get(adaptor)) {
			listener.notify(adaptor, result);
		}
		
	}
	
	public void addAdaptorListener(AdaptorListenerInterface listener) {
		listenersAll.add(listener);
	}
	
	public void addAdaptorListener(AdaptorListenerInterface listener, AdaptorPluginInterface adaptor) {
		listeners.get(adaptor).add(listener);
	}
}
