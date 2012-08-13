package eu.scape_project.watch.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.SchedulerInterface;
import eu.scape_project.watch.linking.DataLinker;
import eu.scape_project.watch.merging.DataMerger;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.scheduling.quartz.QuartzScheduler;
import eu.scape_project.watch.utils.AllDataResultListener;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;

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

  private static final String COMPONENT_CONTAINER = "componentContainer";

  /**
   * The adaptor manager identifier within the servlet context.
   */
  private static final String SCOUT_ADAPTORMANAGER = "scout.core.adaptormanager";

  private static final String SCOUT_DATA_MERGER = "scout.core.datamerger";

  private static final String SCOUT_SCHEDULER = "scout.core.scheduler";

  private static final String SCOUT_DATA_LINKER = "scout.core.datalinker";

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    LOG.info("Preparing Scout for shutdown.");

    final SchedulerInterface scheduler = (SchedulerInterface) sce.getServletContext().getAttribute(SCOUT_SCHEDULER);
    final AdaptorManager manager = (AdaptorManager) sce.getServletContext().getAttribute(SCOUT_ADAPTORMANAGER);
    final Map<String, AdaptorPluginInterface> activeAdaptors = manager.getActiveAdaptorPlugins();

    for (AdaptorPluginInterface adaptor : activeAdaptors.values()) {
      scheduler.stop(adaptor);
    }
    
    scheduler.clear();

    manager.shutdownAll();

    PluginManager.getDefaultPluginManager().shutdown();

    KBUtils.dbDisconnect();

  }

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    LOG.info("Starting Scout");

    // initialize the knowledgebase and make a connection.
    initDB();

    // initialize the PluginManager...
    PluginManager.getDefaultPluginManager();

    // create adaptormanager and load all active adaptors.
    final AdaptorManager manager = new AdaptorManager();
    final Map<String, AdaptorPluginInterface> activeAdaptors = manager.getActiveAdaptorPlugins();

    // create data merger and add it as a listener.
    final DataMerger merger = new DataMerger();
    
    //create data linker
    final DataLinker linker = new DataLinker();
    //TODO add link rules as more adaptors come.
    //TODO create interface for creating these
    //rules

    // create scheduler
    final SchedulerInterface scheduler = new QuartzScheduler();
    final AllDataResultListener resultListener = new AllDataResultListener(manager, merger);
    scheduler.addAdaptorListener(resultListener);

    //TODO read this out of file or some other way...
    final Map<String, String> schedulerConfig = new HashMap<String, String>();
    schedulerConfig.put("scheduler.intervalInSeconds", "300"); //run every 5 minutes...
    
    for (AdaptorPluginInterface adaptor : activeAdaptors.values()) {
      scheduler.start(adaptor, schedulerConfig); // TODO add desired properties...
    }

    saveTestRequest();

    sce.getServletContext().setAttribute(SCOUT_DATA_MERGER, merger);
    sce.getServletContext().setAttribute(SCOUT_DATA_LINKER, linker);
    sce.getServletContext().setAttribute(SCOUT_SCHEDULER, scheduler);
    sce.getServletContext().setAttribute(SCOUT_ADAPTORMANAGER, manager);

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
