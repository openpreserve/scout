package eu.scape_project.watch.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
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
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.domain.SourceAdaptorEventType;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.SchedulerInterface;
import eu.scape_project.watch.linking.DataLinker;
import eu.scape_project.watch.merging.DataMerger;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.policy.PolicyModel;
import eu.scape_project.watch.scheduling.quartz.QuartzScheduler;
import eu.scape_project.watch.utils.AdaptorsLifecycleListener;
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

  private AdaptorManager manager = null;
  private SchedulerInterface scheduler = null;

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    LOG.info("Starting Scout");

    // initialize the knowledgebase and make a connection.
    initDB();

    // initialize the PluginManager...
    PluginManager.getDefaultPluginManager();

    // create adaptormanager and load all active adaptors.
    manager = new AdaptorManager();
    final Map<String, AdaptorPluginInterface> activeAdaptors = manager.getActiveAdaptorPlugins();

    /**
     * TEST DATA try { saveTestRequest(manager); manager.reloadKnownAdaptors();
     * } catch (final Throwable e) { LOG.error("Error saving test request", e);
     * }
     */

    // the policy model of scout.
    final PolicyModel model = new PolicyModel();

    // create data merger and add it as a listener.
    final DataMerger merger = new DataMerger();

    // create data linker
    final DataLinker linker = new DataLinker();
    // TODO add link rules as more adaptors come.
    // TODO create interface for creating these
    // rules

    // create scheduler
    scheduler = new QuartzScheduler();
    scheduler.init();

    // Add scheduler listeners
    final AllDataResultListener resultListener = new AllDataResultListener(manager, merger);
    scheduler.addAdaptorListener(resultListener);

    final AdaptorsLifecycleListener schedulerListener = new AdaptorsLifecycleListener(manager);
    scheduler.addSchedulerListener(schedulerListener);

    for (AdaptorPluginInterface adaptor : activeAdaptors.values()) {
      scheduler.start(adaptor, new SourceAdaptorEvent(SourceAdaptorEventType.STARTED, "Application startup"));
    }

    LOG.info("Setting up the servlet context.");
    final ServletContext context = sce.getServletContext();
    ContextUtil.setAdaptorManager(manager, context);
    ContextUtil.setDataMerger(merger, context);
    ContextUtil.setDataLinker(linker, context);
    ContextUtil.setScheduler(scheduler, context);
    ContextUtil.setPolicyModel(model, context);
  }

  /**
   * Opens up the tdb connection and looks for the current configuration and
   * initializes the data folder if needed. If the knowledgebase is empty some
   * test data is added.
   * 
   */
  private void initDB() {

    final ConfigUtils conf = new ConfigUtils("Knowledge Base");
    final String datafolder = conf.getStringProperty(ConfigUtils.KB_DATA_FOLDER_KEY);
    final boolean initdata = conf.getBooleanProperty(ConfigUtils.KB_INSERT_TEST_DATA);

    KBUtils.dbConnect(datafolder, initdata);

  }

  private void saveTestRequest(final AdaptorManager manager) {
    final EntityType et = new EntityType("CollectionProfile", "");
    final Property prop = new Property(et, "size", "");
    final Entity ent = new Entity(et, "coll-0-test");

    DAO.save(et);
    DAO.save(prop);
    DAO.save(ent);

    final Question question1 = new Question("?s watch:property ?x . "
        + "?x watch:name \"cp.collection.size\"^^xsd:string . "
        + "?s watch:value ?y  FILTER(xsd:integer(?y) > 10000000) ", RequestTarget.PROPERTY_VALUE, Arrays.asList(et),
        Arrays.asList(prop), Arrays.asList(ent), 60);
    final Map<String, String> not1config = new HashMap<String, String>();
    not1config.put("to", "lfaria@keep.pt");
    not1config.put("subject", "SCOUT - WARNING : Collection is over 10 000 000 ");
    final Notification notification1 = new Notification("log", not1config);

    final Trigger trigger1 = new Trigger(question1, Arrays.asList(notification1), null);

    final AsyncRequest asRe = new AsyncRequest();
    asRe.addTrigger(trigger1);
    DAO.save(asRe);

    final Source source = new Source("C3PO", "Content Profile");

    DAO.save(source);

    final Map<String, String> config = new HashMap<String, String>();
    config.put("c3po.endpoint", "dummy");

    final SourceAdaptor c3po = manager.createAdaptor("c3po", "0.0.4", "demo", config, source);
  }

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    LOG.info("Preparing Scout for shutdown.");

    if (manager != null && scheduler != null) {
      final Map<String, AdaptorPluginInterface> activeAdaptors = manager.getActiveAdaptorPlugins();

      for (AdaptorPluginInterface adaptor : activeAdaptors.values()) {
        scheduler.stop(adaptor, null);
      }
      scheduler.shutdown();

      manager.shutdownAll();
    } else {
      LOG.warn("Could not get AdaptorManager or Scheduler from servlet context, skipping adaptor scheduling cleanup");
    }

    PluginManager.getDefaultPluginManager().shutdown();

    KBUtils.dbDisconnect();

  }
}
