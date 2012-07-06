package eu.scape_project.watch.utils;

import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.monitor.CentralMonitor;
import eu.scape_project.watch.scheduling.quartz.QuartzScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for components : CoreScheduler, CentralMonitor, AdaptorLoader
 * 
 * @author kresimir
 * 
 */
public class ComponentContainer {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentContainer.class);

  /**
   * CoreScheduler
   */
  private QuartzScheduler coreScheduler = null;

  /**
   * CentralMonitor
   */
  private CentralMonitor centralMonitor = null;

  /**
   * AdaptorLoader
   */
  private AdaptorLoader adaptorLoader = null;

  /**
   * Default constructor
   */
  public ComponentContainer() {
    LOG.info("ComponentContainer initialized");
  }

  /**
   * Constructor 
   * @param flag - true if it should create default components otherwise false 
   */
  public ComponentContainer(boolean flag) {
    this();
    if (flag) {
      coreScheduler = new QuartzScheduler();
      adaptorLoader = new AdaptorLoader();
      centralMonitor = new CentralMonitor();
    }
  }

  /**
   * Start components which are declared.
   */
  public void init() {
    if (coreScheduler != null){
      coreScheduler.start();
    }
    if (adaptorLoader != null){
      adaptorLoader.startLoader();
    }
  }

  /**
   * Stop components
   */
  public void destroy() {
    if (coreScheduler != null){
      coreScheduler.shutdown();
    }
    if (adaptorLoader != null){
      adaptorLoader.cancelLoader();
    }
  }

  /**
   * Add new Monitor to the system  
   * @param monitor - Monitor to be added
   */
  public void addMonitor(MonitorInterface monitor) {
    if (centralMonitor != null){
      centralMonitor.addMonitor(monitor);
    }
    if (coreScheduler != null) {
      coreScheduler.addGroupJobListener(monitor, monitor.getGroup());
      monitor.registerScheduler(coreScheduler);
    }
  }

  public QuartzScheduler getCoreScheduler() {
    return coreScheduler;
  }

  public void setCoreScheduler(QuartzScheduler coreScheduler) {
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
