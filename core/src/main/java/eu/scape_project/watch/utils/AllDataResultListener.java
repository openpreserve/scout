package eu.scape_project.watch.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.merging.DataMerger;

/**
 * A simple adaptor listener, that passes all obtained results to the
 * datamerger.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class AllDataResultListener implements AdaptorListenerInterface {

  /**
   * Default logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(AllDataResultListener.class);

  /**
   * A reference to the adaptor manager, in order to get the source adaptor
   * provenance information.
   */
  private AdaptorManager manager;

  /**
   * A reference to the data merger to pass the results for further processing.
   */
  private DataMerger merger;

  /**
   * A constructor for the listener.
   * 
   * @param manager
   *          the adaptor manager instance.
   * @param merger
   *          the data merger instance.
   */
  public AllDataResultListener(final AdaptorManager manager, final DataMerger merger) {
    this.manager = manager;
    this.merger = merger;
  }

  /**
   * {@inheritDoc}
   * 
   * Passes all results to the data merger.
   */
  @Override
  public void notify(AdaptorPluginInterface adaptor, ResultInterface result) {
    LOG.info("Received results from adaptor [{}-{}]", adaptor.getName(), adaptor.getVersion());
    Map<String, AdaptorPluginInterface> activePlugins = this.manager.getActiveAdaptorPlugins();

    String instance = "";
    for (String key : activePlugins.keySet()) {
      AdaptorPluginInterface adpt = activePlugins.get(key);
      if (adaptor.equals(adpt)) {
        instance = key;
        break;
      }
    }

    SourceAdaptor sourceAdaptor = this.manager.getSourceAdaptor(instance);

    if (sourceAdaptor == null) {
      LOG.warn("No source adaptor class found for instance id: " + instance);
    } else {
      this.initiateMerging(sourceAdaptor, result);
    }
  }

  /**
   * The actual passing of the results the merger.
   * 
   * @param adaptor
   *          the adaptor provenance information.
   * @param result
   *          the actual result.
   */
  private void initiateMerging(SourceAdaptor adaptor, ResultInterface result) {
    LOG.debug("Start merging data [{}] [{}]", result.getEntity(), result.getValue());
    this.merger.merge(result.getEntity());
    this.merger.merge(adaptor, result.getValue());
  }

}
