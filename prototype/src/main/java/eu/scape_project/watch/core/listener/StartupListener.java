package eu.scape_project.watch.core.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.CentralMonitor;
import eu.scape_project.watch.components.interfaces.MonitorInterface;
import eu.scape_project.watch.components.listeners.CollectionProfilerListener;
import eu.scape_project.watch.core.ComponentContainer;
import eu.scape_project.watch.core.CoreScheduler;
import eu.scape_project.watch.core.loader.AdaptorLoader;
import eu.scape_project.watch.core.plugin.PluginManager;

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
