package eu.scape_project.watch.listener;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.monitor.CentralMonitor;
import eu.scape_project.watch.monitor.CollectionProfilerMonitor;
import eu.scape_project.watch.notification.NotificationService;
import eu.scape_project.watch.scheduling.CoreScheduler;
import eu.scape_project.watch.utils.AdaptorLoader;
import eu.scape_project.watch.utils.ComponentContainer;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
    cm.registerToAsyncRequest();
    cm.setNotificationService(NotificationService.getInstance());

    componentContainer.setCoreScheduler(new CoreScheduler());
    componentContainer.setCentralMonitor(cm);
    componentContainer.setAdaptorLoader(new AdaptorLoader());
    componentContainer.addMonitor(monitor);

    // NotificationService.getInstance().addAdaptor(new
    // LogNotificationAdaptor());
    saveTestRequest();

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

  private void saveTestRequest() {
    EntityType et = new EntityType("CollectionProfile", "");
    Property prop = new Property(et, "size", "");
    Entity ent = new Entity(et, "coll-0-test");
    Question question1 = new Question(
      "?s watch:property ?x . ?x watch:name \"cp.collection.size\"^^xsd:string . ?s watch:value ?y  FILTER(xsd:integer(?y) > 10000000) ",
      RequestTarget.PROPERTY_VALUE, Arrays.asList(et), Arrays.asList(prop), Arrays.asList(ent), 60);
    Map<String, String> not1config = new HashMap<String, String>();
    not1config.put("to", "lfaria@keep.pt");
    not1config.put("subject", "SCOUT - WARNING : Collection is over 10 000 000 ");
    Notification notification1 = new Notification("log", not1config);

    final Trigger trigger1 = new Trigger(question1, Arrays.asList(notification1), null);

    AsyncRequest asRe = new AsyncRequest();
    asRe.addTrigger(trigger1);
    DAO.save(asRe);
  }
}
