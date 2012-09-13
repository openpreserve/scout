package eu.scape_project.watch.scheduling.quartz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;

/**
 * This class is responsible for managing adaptor listeners. 
 * @author Kresimir Duretec <duretec@ifs.tuwien.ac.at>
 *
 */
public class QuartzListenerManager {

  private static final Logger LOG = LoggerFactory.getLogger(QuartzListenerManager.class);

  private List<AdaptorListenerInterface> listenersAll;

  private Map<AdaptorPluginInterface, List<AdaptorListenerInterface>> listeners;

  public QuartzListenerManager() {
    
    listenersAll = new ArrayList<AdaptorListenerInterface>();
    listeners =    new HashMap<AdaptorPluginInterface, List<AdaptorListenerInterface>>();
    
  }
  
  public void notify(AdaptorPluginInterface adaptor, ResultInterface result) {

    for (AdaptorListenerInterface listener : listenersAll) {
      listener.notify(adaptor, result);
    }

    List<AdaptorListenerInterface> list = listeners.get(adaptor);
    if (list != null) {
      for (AdaptorListenerInterface listener : list) {
        listener.notify(adaptor, result);
      }
    }

  }

  public void addAdaptorListener(AdaptorListenerInterface listener) {
    listenersAll.add(listener);
  }

  public void addAdaptorListener(AdaptorListenerInterface listener, AdaptorPluginInterface adaptor) {
    if (listeners.containsKey(adaptor)){
      listeners.get(adaptor).add(listener);
    }else {
      List<AdaptorListenerInterface> list = new ArrayList<AdaptorListenerInterface>();
      list.add(listener);
      listeners.put(adaptor, list);
    }
  }
  
  public void removeAdaptorListener(AdaptorListenerInterface listener) {
    
    listenersAll.remove(listener);
    
    for (AdaptorPluginInterface ap : listeners.keySet()) {
      listeners.get(ap).remove(listener);
    }
    
  }
  
}
