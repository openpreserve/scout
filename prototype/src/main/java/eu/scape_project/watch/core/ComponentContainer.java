package eu.scape_project.watch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.CentralMonitor;
import eu.scape_project.watch.components.interfaces.MonitorInterface;
import eu.scape_project.watch.core.loader.AdaptorLoader;

/**
 * Container for components
 * 
 * @author kresimir
 * 
 */
public class ComponentContainer {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentContainer.class);

  private CoreScheduler coreScheduler = null;

  private CentralMonitor centralMonitor = null;

  private AdaptorLoader adaptorLoader = null;

  /**
   * constructor which does nothing at the moment
   */
  public ComponentContainer() {
    LOG.info("ComponentContainer initialized");
  }

  public ComponentContainer(boolean flag) {
    this();
    if (flag) {
      coreScheduler = new CoreScheduler();
      adaptorLoader = new AdaptorLoader();
      centralMonitor = new CentralMonitor();
    }
  }

  /**
   * starting components
   */
  public void init() {
    // TODO add else throw exception
    if (coreScheduler != null)
      coreScheduler.start();
    if (adaptorLoader != null)
      adaptorLoader.startLoader();
  }

  public void destroy() {
    // TODO add else throw exception
    if (coreScheduler != null)
      coreScheduler.shutdown();
    if (adaptorLoader != null)
      adaptorLoader.cancelLoader();
  }

  public void addMonitor(MonitorInterface monitor) {
    // TODO add throw exception
    if (centralMonitor != null)
      centralMonitor.addMonitor(monitor);
    if (coreScheduler != null) {
      coreScheduler.adddGroupJobListener(monitor, monitor.getGroup());
      monitor.registerScheduler(coreScheduler);
    }
  }

  public CoreScheduler getCoreScheduler() {
    return coreScheduler;
  }

  public void setCoreScheduler(CoreScheduler coreScheduler) {
    this.coreScheduler = coreScheduler;
  }

  public CentralMonitor getCentralMonitor() {
    return centralMonitor;
  }

  public void setCentralMonitor(CentralMonitor centralMonitor) {
    this.centralMonitor = centralMonitor;
  }

  public AdaptorLoader getAdaptorLoader() {
    return adaptorLoader;
  }

  public void setAdaptorLoader(AdaptorLoader adaptorLoader) {
    this.adaptorLoader = adaptorLoader;
    this.adaptorLoader.setCoreScheduler(coreScheduler);
  }

}
