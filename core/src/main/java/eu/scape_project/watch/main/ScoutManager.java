package eu.scape_project.watch.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.QuestionTemplate;
import eu.scape_project.watch.domain.QuestionTemplateParameter;
import eu.scape_project.watch.domain.QuestionTemplateParameter.ParameterType;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.RenderingHint;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.domain.SourceAdaptorEventType;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.NotificationPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.interfaces.SchedulerInterface;
import eu.scape_project.watch.interfaces.eventhandling.ScoutChangeEvent;
import eu.scape_project.watch.interfaces.eventhandling.ScoutComponentListener;
import eu.scape_project.watch.linking.DataLinker;
import eu.scape_project.watch.merging.DataMerger;
import eu.scape_project.watch.notification.NotificationService;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.policy.PolicyModel;
import eu.scape_project.watch.scheduling.quartz.QuartzScheduler;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;

public class ScoutManager {
  /**
   * A default logger for this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ScoutManager.class);

  private AdaptorManager adaptorManager;
  private SchedulerInterface scheduler;
  private DataMerger dataMerger;
  private DataLinker dataLinker;
  private PolicyModel policyModel;
  private NotificationService notificationService;

  public ScoutManager() {

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

  public void start() {
    // initialize the knowledgebase and make a connection.
    initDB();

    // initialize the PluginManager...
    final PluginManager pluginManager = PluginManager.getDefaultPluginManager();

    // create adaptormanager and load all active adaptors.
    adaptorManager = new AdaptorManager();
    final Map<String, AdaptorPluginInterface> activeAdaptors = adaptorManager.getActiveAdaptorPlugins();

    /**
     * TEST DATA try { saveTestRequest(manager); manager.reloadKnownAdaptors();
     * } catch (final Throwable e) { LOG.error("Error saving test request", e);
     * }
     */

    // create data merger and add it as a listener.
    dataMerger = new DataMerger();

    // create data linker
    dataLinker = new DataLinker();
    // TODO add link rules as more adaptors come.
    // TODO create interface for creating these rules

    notificationService = new NotificationService();
    final AssessmentService assessmentService = new AssessmentService(notificationService);
    final RequestToDataBinder requestToDataBinder = new RequestToDataBinder(assessmentService);

    // Register all notification plugins in notification service
    final List<PluginInfo> notificationPluginInfos = pluginManager.getPluginInfo(PluginType.NOTIFICATION);

    for (PluginInfo notificationPluginInfo : notificationPluginInfos) {
      final NotificationPluginInterface notificationPlugin = (NotificationPluginInterface) pluginManager.getPlugin(
        notificationPluginInfo.getClassName(), notificationPluginInfo.getVersion());
      notificationService.addAdaptor(notificationPlugin);
    }

    pluginManager.addObserver(new ScoutComponentListener() {

      @Override
      public void onChange(ScoutChangeEvent evt) {
        // XXX Event only occurs when plugin is added
        Object message = evt.getMessage();
        if (message instanceof PluginInfo) {
          final PluginInfo pluginInfo = (PluginInfo) message;
          if (pluginInfo.getType().equals(PluginType.NOTIFICATION)) {
            final NotificationPluginInterface notificationPlugin = (NotificationPluginInterface) pluginManager
              .getPlugin(pluginInfo.getClassName(), pluginInfo.getVersion());
            notificationService.addAdaptor(notificationPlugin);
          }
        }
      }
    });

    // TODO Detect removed notification plugins and unregister them from
    // notification service

    // the policy model of scout
    policyModel = new PolicyModel();

    // create scheduler
    scheduler = new QuartzScheduler(assessmentService);
    scheduler.init();

    // Add adaptors listeners
    final AdaptorsFetchedDataListener resultListener = new AdaptorsFetchedDataListener(adaptorManager, dataMerger);
    scheduler.addAdaptorListener(resultListener);

    final AdaptorsLifecycleListener schedulerListener = new AdaptorsLifecycleListener(adaptorManager);
    scheduler.addSchedulerListener(schedulerListener);

    // Schedule active adaptors
    for (AdaptorPluginInterface adaptor : activeAdaptors.values()) {
      scheduler.startAdaptor(adaptor, new SourceAdaptorEvent(SourceAdaptorEventType.STARTED, "Application startup"));
    }

    // Monitor adaptors
    DAO.addDOListener(SourceAdaptor.class, new DOListener<SourceAdaptor>() {

      @Override
      public void onUpdated(SourceAdaptor adaptor) {
        AdaptorPluginInterface adaptorPluginInstance = adaptorManager.findAdaptorPluginInstance(adaptor.getInstance());

        if (adaptorPluginInstance == null) {
          // new adaptor
          adaptorPluginInstance = adaptorManager.createAdaptorInstance(adaptor.getInstance());
          if (adaptor.isActive()) {
            scheduler.startAdaptor(adaptorPluginInstance, new SourceAdaptorEvent(SourceAdaptorEventType.STARTED,
              "Source adaptor was created"));
          }
        } else {
          // updated adaptor
          if (adaptor.isActive()) {
            scheduler.startAdaptor(adaptorPluginInstance, new SourceAdaptorEvent(SourceAdaptorEventType.STARTED,
              "Source adaptor was activated"));
          } else {
            scheduler.stopAdaptor(adaptorPluginInstance, new SourceAdaptorEvent(SourceAdaptorEventType.STOPPED,
              "Source adaptor was de-activated"));
          }
        }
      }

      @Override
      public void onRemoved(SourceAdaptor adaptor) {
        final AdaptorPluginInterface adaptorInstance = adaptorManager.findAdaptorPluginInstance(adaptor.getInstance());
        scheduler.stopAdaptor(adaptorInstance, new SourceAdaptorEvent(SourceAdaptorEventType.STOPPED,
          "Source adaptor was removed"));
        // adaptorManager.reloadKnownAdaptors();
      }
    });

    // Initialize current requests
    final int requestCount = DAO.ASYNC_REQUEST.count("");

    int i = 0;
    while (i < requestCount) {
      final List<AsyncRequest> requests = DAO.ASYNC_REQUEST.list(i, 100);
      i += 100;

      for (final AsyncRequest request : requests) {
        addRequest(request, requestToDataBinder);
      }
    }

    // Monitor requests CRUD
    DAO.addDOListener(AsyncRequest.class, new DOListener<AsyncRequest>() {

      @Override
      public void onUpdated(final AsyncRequest request) {
        LOG.info("Request update detected: {}", request);
        removeRequest(request, requestToDataBinder);
        addRequest(request, requestToDataBinder);
      }

      @Override
      public void onRemoved(final AsyncRequest request) {
        LOG.info("Request delete detected: {}", request);
        removeRequest(request, requestToDataBinder);
      }
    });

    // TODO remove question template test
    List<QuestionTemplate> templates = DAO.QUESTION_TEMPLATE.list(0, DAO.QUESTION_TEMPLATE.count(""));
    DAO.delete(templates.toArray(new QuestionTemplate[templates.size()]));
    
    final String title1 = "Collection size limit";
    final String description1 = "Warn when a collection reaches a determinated storage size threshold";
    final String sparql1 = "?s watch:id ?id . ?s watch:longValue ?value . FILTER(?value > ?threshold)";
    final RequestTarget target1 = RequestTarget.PROPERTY_VALUE;
    final List<QuestionTemplateParameter> parameters1 = new ArrayList<QuestionTemplateParameter>();
    parameters1.add(new QuestionTemplateParameter("id", "Collection",
      "Your collection profile already inserted into scout", ParameterType.NODE,
      "?s watch:property ?p . ? watch:id \"3lkHQ_nkayLHyyqDwmL9R4hF6jQ\"^^xsd:string", RequestTarget.PROPERTY_VALUE,
      null, null));
    parameters1.add(new QuestionTemplateParameter("threshold", "Storage size threshold",
      "The storage size above which to raise the alert", ParameterType.LITERAL, null, null, DataType.LONG,
      RenderingHint.STORAGE_VOLUME));
    final QuestionTemplate template1 = new QuestionTemplate(title1, description1, sparql1, target1, parameters1);
    DAO.save(template1);

    final String title2 = "File format is withdrawn";
    final String description2 = "Warn when a file format in my collection is marked as withdrawn";
    final String sparql2 = "?s watch:property ?nameproperty . " + "?nameproperty watch:name \"mime\"^^xsd:string . "
      + "?withdrawn rdf:type watch:PropertyValue . " + "?withdrawn watch:entity ?entity . "
      + "?withdrawn watch:property ?withdrawnproperty . "
      + "?withdrawnproperty watch:id \"gszzE-ZmAKXKxsqeesWjKw8v_8A\"^^xsd:string";
    final RequestTarget target2 = RequestTarget.PROPERTY_VALUE;
    final List<QuestionTemplateParameter> parameters2 = new ArrayList<QuestionTemplateParameter>();
    final QuestionTemplate template2 = new QuestionTemplate(title2, description2, sparql2, target2, parameters2);
    DAO.save(template2);

  }

  private void addRequest(final AsyncRequest request, final RequestToDataBinder requestToDataBinder) {
    LOG.info("Adding request to data binding and scheduling: {}", request);
    requestToDataBinder.bindRequest(request);
    scheduler.startRequest(request);
  }

  private void removeRequest(final AsyncRequest request, final RequestToDataBinder requestToDataBinder) {
    requestToDataBinder.unbindRequest(request);
    scheduler.stopRequest(request);
  }

  public void stop() {
    if (adaptorManager != null && scheduler != null) {
      final Map<String, AdaptorPluginInterface> activeAdaptors = adaptorManager.getActiveAdaptorPlugins();

      for (AdaptorPluginInterface adaptor : activeAdaptors.values()) {
        scheduler.stopAdaptor(adaptor, null);
      }
      final int requestCount = DAO.ASYNC_REQUEST.count("");
      List<AsyncRequest> requests = DAO.ASYNC_REQUEST.list(0, requestCount);

      for (AsyncRequest request : requests) {
        scheduler.stopRequest(request);
      }

      scheduler.shutdown();

      adaptorManager.shutdownAll();
    } else {
      LOG.warn("Could not get AdaptorManager or Scheduler from servlet context, skipping adaptor scheduling cleanup");
    }

    DAO.clearDOListeners();

    PluginManager.getDefaultPluginManager().shutdown();

    KBUtils.dbDisconnect();
  }

  public AdaptorManager getAdaptorManager() {
    return adaptorManager;
  }

  public SchedulerInterface getScheduler() {
    return scheduler;
  }

  public DataMerger getDataMerger() {
    return dataMerger;
  }

  public DataLinker getDataLinker() {
    return dataLinker;
  }

  public PolicyModel getPolicyModel() {
    return policyModel;
  }

  public NotificationService getNotificationService() {
    return notificationService;
  }

}
