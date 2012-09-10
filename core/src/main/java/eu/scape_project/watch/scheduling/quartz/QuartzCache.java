package eu.scape_project.watch.scheduling.quartz;

import java.util.HashMap;
import java.util.Map;

import org.openjena.atlas.logging.Log;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorPluginInterface;

/**
 * This class is used by QuartzScheduler
 * @author Kresimir Duretec <duretec@ifs.tuwien.ac.at>
 *
 */
public class QuartzCache {
  
  private static final Logger LOG = LoggerFactory.getLogger(QuartzCache.class);

  private Map<AdaptorPluginInterface, AdaptorState> aStates;

  private Map<String, AdaptorPluginInterface> adaptors;

  public QuartzCache() {
    aStates = new HashMap<AdaptorPluginInterface, AdaptorState>();
    adaptors = new HashMap<String, AdaptorPluginInterface>();
  }

  public AdaptorPluginInterface getAdaptorPluginInterface(String id) {
    AdaptorPluginInterface tmp = adaptors.get(id);
    // TODO case where tmp==null
    return tmp;
  }

  public JobKey getAdaptorJobKey(AdaptorPluginInterface adaptor) {
    AdaptorState as = aStates.get(adaptor);
    JobKey jk = as.getJobKey();
    return jk;
  }

  public String getAdaptorId(AdaptorPluginInterface adaptor) {
    AdaptorState as = aStates.get(adaptor);
    String id = as.getId();
    return id;
  }

  public String addAdaptorPlugin(AdaptorPluginInterface adaptor) {
    if (!aStates.containsKey(adaptor)) {
      AdaptorState as = new AdaptorState();
      aStates.put(adaptor, as);
      adaptors.put(as.getId(), adaptor);
      return as.getId();
    } else {
      // TODO
      return null;
    }
  }

  public void removeAdaptorPlugin(AdaptorPluginInterface adaptor) {
    if (aStates.containsKey(adaptor)) {
      AdaptorState as = aStates.get(adaptor);
      String id = as.getId();
      aStates.remove(adaptor);
      adaptors.remove(id);
    } else {
      LOG.warn(adaptor.getName() + " is not in cache,  nothing to remove!");
    }
  }

  public void addJobKey(AdaptorPluginInterface adaptor, JobKey jk) {
    AdaptorState as = aStates.get(adaptor);
    as.setJobKey(jk);
  }

  public boolean containsAdaptor(AdaptorPluginInterface adaptor) {
    return aStates.containsKey(adaptor);
  }

  public boolean containsAdaptorId(String id) {
    return adaptors.containsKey(id);
  }
}