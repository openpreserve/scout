package eu.scape_project.watch.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    LOG.info("Destroying Watch Application context");
    destroy();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    LOG.info("Starting Watch Application");
    init();
  }

  /**
   * Opens up the tdb connection and looks for the current configuration and
   * initializes the data folder if needed. If the knowledgebase is empty some
   * test data is added.
   * 
   */
  private void init() {
    final ConfigUtils conf = new ConfigUtils();
    final String datafolder = conf.getStringProperty(ConfigUtils.KB_DATA_FOLDER_KEY);
    final boolean initdata = conf.getBooleanProperty(ConfigUtils.KB_INSERT_TEST_DATA);

    KBUtils.dbConnect(datafolder, initdata);
  }

  /**
   * Syncs the jena model and closes it.
   */
  private void destroy() {
    KBUtils.dbDisconnect();
  }

}
