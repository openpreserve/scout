package eu.scape_project.watch.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.monitor.CentralMonitor;
import eu.scape_project.watch.monitor.CollectionProfilerMonitor;
import eu.scape_project.watch.scheduling.CoreScheduler;
import eu.scape_project.watch.utils.AdaptorLoader;
import eu.scape_project.watch.utils.ComponentContainer;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;

/**
 * An application startup listener, that is invoked by the container on
 * application launch. It is currently used to initialize and insert some test
 * data.
 * 
 * @author petar
 * 
 */
public class ApplicationStartupListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartupListener.class);
  private static final String COMPONENT_CONTAINER = "componentContainer";

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    LOG.info("Destroying Watch Application context");

    final ComponentContainer componentContainer = (ComponentContainer) sce.getServletContext().getAttribute(
      COMPONENT_CONTAINER);
    componentContainer.destroy();

    KBUtils.dbDisconnect();
  }

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    LOG.info("Starting Scout Application");
    initDB();

    LOG.debug("Starting up core components");
    final ComponentContainer componentContainer = new ComponentContainer();
    final MonitorInterface monitor = new CollectionProfilerMonitor();

    CentralMonitor cm = new CentralMonitor();
    
    componentContainer.setCoreScheduler(new CoreScheduler());
    componentContainer.setCentralMonitor(cm);
    componentContainer.setAdaptorLoader(new AdaptorLoader());
    componentContainer.addMonitor(monitor);

    AsyncRequest asRe = new AsyncRequest();
    cm.onUpdated(asRe);
    
    componentContainer.init();

    sce.getServletContext().setAttribute(COMPONENT_CONTAINER, componentContainer);
  }

  /**
   * Opens up the tdb connection and looks for the current configuration and
   * initializes the data folder if needed. If the knowledgebase is empty some
   * test data is added.
   * 
   */
  private void initDB() {
    final ConfigUtils conf = new ConfigUtils();
    final String datafolder = conf.getStringProperty(ConfigUtils.KB_DATA_FOLDER_KEY);
    final boolean initdata = conf.getBooleanProperty(ConfigUtils.KB_INSERT_TEST_DATA);

    KBUtils.dbConnect(datafolder, initdata);
  }

}
