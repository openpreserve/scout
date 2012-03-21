package eu.scape_project.watch.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.CentralMonitor;
<<<<<<< HEAD:prototype/src/main/java/eu/scape_project/watch/core/listener/StartupListener.java
import eu.scape_project.watch.components.listeners.CollectionProfilerListener;
=======
>>>>>>> adding new projects:prototype/src/main/java/eu/scape_project/watch/listener/StartupListener.java
import eu.scape_project.watch.core.ComponentContainer;
import eu.scape_project.watch.core.CoreScheduler;
import eu.scape_project.watch.interfaces.IMonitor;
import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.listener.CollectionProfilerListener;
import eu.scape_project.watch.loader.AdaptorLoader;
import eu.scape_project.watch.plugin.PluginManager;

/**
 * 
 * @author kresimir
 * 
 */
public class StartupListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    ComponentContainer componentContainer = (ComponentContainer) sce.getServletContext().getAttribute("componentContainer");
    componentContainer.destroy();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    LOG.info("Starting up core components");
    
    
    ComponentContainer componentContainer = new ComponentContainer();
    
    componentContainer.setCoreScheduler(new CoreScheduler());
    componentContainer.setCentralMonitor(new CentralMonitor());
    componentContainer.setAdaptorLoader(new AdaptorLoader());
    
    
    MonitorInterface monitor = new CollectionProfilerListener();
    componentContainer.addMonitor(monitor); 
    
    componentContainer.init();
    
    sce.getServletContext().setAttribute("componentContainer", componentContainer);
    
    
  }

}
