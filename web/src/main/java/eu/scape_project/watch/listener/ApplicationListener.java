package eu.scape_project.watch.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.main.ScoutManager;

/**
 * An application startup listener, that is invoked by the container on
 * application launch. It is currently used to initialize and insert some test
 * data.
 * 
 * @author Petar Petrov - <me@petarpetrov.org>
 * 
 */
public class ApplicationListener implements ServletContextListener {

  /**
   * A default logger for this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ApplicationListener.class);

  private ScoutManager scoutManager;

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    LOG.info("Starting Scout");

    scoutManager = new ScoutManager();
    scoutManager.start();

    LOG.info("Setting up the servlet context.");
    final ServletContext context = sce.getServletContext();
    ContextUtil.setScoutManager(scoutManager, context);
  }

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    LOG.info("Preparing Scout for shutdown.");
    scoutManager.stop();
  }
}
