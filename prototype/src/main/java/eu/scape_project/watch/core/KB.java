package eu.scape_project.watch.core;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import eu.scape_project.watch.core.common.ConfigUtils;
import eu.scape_project.watch.core.model.AsyncRequest;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Notification;
import eu.scape_project.watch.core.model.NotificationType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;
import eu.scape_project.watch.core.model.Question;
import eu.scape_project.watch.core.model.RequestTarget;
import eu.scape_project.watch.core.model.Trigger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;

/**
 * This class provides a single point of reference for knowledge base management
 * operations.
 * 
 * @author Watch Dev Team
 */
public final class KB {

  /**
   * A default logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(KB.class);

  /**
   * The singletons unique instance object.
   */
  private static KB uniqueInstance;

  /**
   * Jena's Dataset.
   */
  private static Dataset dataset;

  /**
   * The model we use to store the kb objects.
   */
  private static Model model;

  /**
   * The preservation watch namespace.
   */
  public static final String WATCH_NS = "http://watch.scape-project.eu/kb#";

  /**
   * The default RDF syntax namespace.
   */
  public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

  /**
   * The default XSD syntax namespace.
   */
  public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";

  /**
   * Helper method to create a prefix declaration for SPARQL queries.
   * 
   * @param prefix
   *          The prefix to use
   * @param namescape
   *          The namespace to be prefixed
   * @return the prefix declarion with a new line on the end
   */
  private static String createPrefixDecl(final String prefix, final String namescape) {
    return "PREFIX " + prefix + " <" + namescape + ">\n";
  }

  /**
   * The XSD prefix to use in SPARQL queries.
   */
  public static final String XSD_PREFIX = "xsd:";
  /**
   * The declaration of the XSD prefix to use in SPARQL queries.
   */
  public static final String XSD_PREFIX_DECL = createPrefixDecl(XSD_PREFIX, KB.XSD_NS);

  /**
   * The RDF prefix to use in SPARQL queries.
   */
  public static final String RDF_PREFIX = "rdf:";
  /**
   * The declaration of the RDF prefix to use in SPARQL queries.
   */
  public static final String RDF_PREFIX_DECL = createPrefixDecl(RDF_PREFIX, KB.RDF_NS);

  /**
   * The WATCH prefix to use in SPARQL queries.
   */
  public static final String WATCH_PREFIX = "watch:";
  /**
   * The declaration of the WATCH prefix to use in SPARQL queries.
   */
  public static final String WATCH_PREFIX_DECL = createPrefixDecl(WATCH_PREFIX, KB.WATCH_NS);

  private static <T extends RdfBean<T>> String getResourcePrefix(Class<T> resourceClass) {
    return "watch-" + resourceClass.getSimpleName() + ":";
  }

  private static <T extends RdfBean<T>> String getResourceNamespace(Class<T> resourceClass) {
    return WATCH_NS + resourceClass.getSimpleName() + "/";
  }

  private static <T extends RdfBean<T>> String getResourcePrefixDecl(Class<T> resourceClass) {
    return createPrefixDecl(getResourcePrefix(resourceClass), getResourceNamespace(resourceClass));
  }

  public static final String WATCH_ENTITY_TYPE_PREFIX = getResourcePrefix(EntityType.class);
  public static final String WATCH_ENTITY_TYPE_PREFIX_DECL = getResourcePrefixDecl(EntityType.class);
  public static final String WATCH_PROPERTY_PREFIX = getResourcePrefix(Property.class);
  public static final String WATCH_PROPERTY_PREFIX_DECL = getResourcePrefixDecl(Property.class);
  public static final String WATCH_ENTITY_PREFIX = getResourcePrefix(Entity.class);
  public static final String WATCH_ENTITY_PREFIX_DECL = getResourcePrefixDecl(Entity.class);
  public static final String WATCH_PROPERTY_VALUE_PREFIX = getResourcePrefix(PropertyValue.class);
  public static final String WATCH_PROPERTY_VALUE_PREFIX_DECL = getResourcePrefixDecl(PropertyValue.class);

  public static final String PREFIXES_DECL = XSD_PREFIX_DECL + RDF_PREFIX_DECL + WATCH_PREFIX_DECL
    + WATCH_ENTITY_TYPE_PREFIX_DECL + WATCH_PROPERTY_PREFIX_DECL + WATCH_ENTITY_PREFIX_DECL
    + WATCH_PROPERTY_VALUE_PREFIX_DECL;

  /**
   * The RDF type relation.
   */
  public static final String RDF_TYPE_REL = RDF_PREFIX + "type";

  /**
   * An entity constant.
   */
  public static final String ENTITY = "entity";

  /**
   * An entity type constant.
   */
  public static final String ENTITY_TYPE = "entitytype";

  /**
   * A property constant.
   */
  public static final String PROPERTY = "property";

  /**
   * A propertyvalue constant.
   */
  public static final String PROPERTY_VALUE = "propertyvalue";

  /**
   * A measurement constant.
   */
  public static final String MEASUREMENT = "measurement";

  /**
   * A asynchronous request constant.
   */
  public static final String ASYNC_REQUEST = "asyncrequest";

  /**
   * A trigger constant.
   */
  public static final String TRIGGER = "trigger";

  /**
   * A notification constant.
   */
  public static final String NOTIFICATION = "notification";

  /**
   * A notification constant.
   */
  public static final String NOTIFICATION_TYPE = "notificationtype";

  /**
   * A data type constant.
   */
  public static final String DATA_TYPE = "datatype";

  /**
   * A plan constant.
   */
  public static final String PLAN = "plan";

  /**
   * A question constant.
   */
  public static final String QUESTION = "question";

  /**
   * A request target constant.
   */
  public static final String REQUEST_TARGET = "target";

  /**
   * A source constant.
   */
  public static final String SOURCE = "source";

  /**
   * A source adaptor constant.
   */
  public static final String SOURCE_ADAPTOR = "sourceadaptor";

  /**
   * A key-value pair entry constant.
   */
  public static final String ENTRY = "entry";

  /**
   * The data folder to be used to by tdb.
   */
  private static String dataFolder;

  /**
   * Sets the data folder to a new one.
   * 
   * @param dataFolder
   *          the new data folder to use.
   */
  public static void setDataFolder(final String dataFolder) {
    KB.dataFolder = dataFolder;
  }

  /**
   * Follows the singleton pattern and retrieves a unique KB instance. If the
   * instance is not yet initialized the necessarry steps to do so are done.
   * 
   * @return a unique KB instance
   */
  public static synchronized KB getInstance() {
    if (KB.uniqueInstance == null) {
      KB.uniqueInstance = new KB();
      KB.init();
      KB.addShutDownHook();
    }

    return KB.uniqueInstance;
  }

  public Dataset getDataset() {
    return KB.dataset;
  }

  public Model getModel() {
    return model;
  }

  public RDF2Bean getReader() {
    return Jenabean.instance().reader();
  }

  public Bean2RDF getWriter() {
    return Jenabean.instance().writer();
  }

  /**
   * {@inheritDoc}
   * 
   * Always throws an exception as singletons cannot be cloned.
   * 
   * @return always throws exception.
   * @throws CloneNotSupportedException
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Singletons cannot be cloned");
  }

  // --- private

  /**
   * Opens up the tdb connection and looks for the current configuration and
   * initializes the data folder if needed. If the knowledgebase is empty some
   * test data is added.
   * 
   */
  private static void init() {

    if (dataFolder == null) {
      final ConfigUtils conf = new ConfigUtils();
      setDataFolder(conf.getStringProperty(ConfigUtils.KB_DATA_FOLDER_KEY));
    }

    final File dataFolderFile = new File(dataFolder);
    try {
      boolean initdata = false;
      if (!dataFolderFile.exists()) {
        FileUtils.forceMkdir(dataFolderFile);
        initdata = true;
      }

      dataset = TDBFactory.createDataset(dataFolder);
      model = TDBFactory.createModel(dataFolder);
      Jenabean.instance().bind(model);

      if (initdata) {
        createInitialData();
      }

      LOGGER.info("KB manager created at {}", dataFolder);

      KBUtils.printStatements();

    } catch (final IOException e) {
      LOGGER.error("Data folder {} could not be created", e.getMessage());
    }
  }

  /**
   * Adds a shutdown hook to the runtime, so that TDB can be closed in a clean
   * fashion upon server shutdown.
   */
  private static void addShutDownHook() {
    KB.LOGGER.debug("adding shutdown hook to Knowledgebase");

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        KB.LOGGER.info("Shutdown hook invoked, closing dataset");

        if (dataset != null) {
          flush();

          model.close();
          dataset.close();
        }
      }
    }));
  }

  /**
   * Synchronizes the current state of the Jena Cache with the underlying TDB.
   */
  private static void flush() {
    TDB.sync(model);
    TDB.sync(dataset);
  }

  /**
   * Creates some initial data.
   */
  private static void createInitialData() {
    LOGGER.info("Creating data");
    final EntityType formats = new EntityType("format", "File format");
    final Property formatPUID = new Property(formats, "PUID", "PRONOM Id");
    final Property formatMimetype = new Property(formats, "MIME", "MIME type");

    formats.save();
    formatPUID.save();
    formatMimetype.save();

    final EntityType tools = new EntityType("tools", "Applications that read and/or write into diferent file formats");
    final Property toolVersion = new Property(tools, "version", "Tool version");

    tools.save();
    toolVersion.save();

    final Entity pdf17Format = new Entity(formats, "PDF-v1.7");
    final Entity tiffFormat = new Entity(formats, "TIFF");
    final Entity imageMagickTool = new Entity(tools, "ImageMagick-v6.6.0");

    // save entities
    pdf17Format.save();
    tiffFormat.save();
    imageMagickTool.save();

    // property value construction also binds to entity
    final PropertyValue pdfPUID = new PropertyValue(pdf17Format, formatPUID, "fmt/276");
    final PropertyValue pdfMime = new PropertyValue(pdf17Format, formatMimetype, "application/pdf");

    final PropertyValue tiffPUID = new PropertyValue(tiffFormat, formatPUID, "fmt/353");
    final PropertyValue tiffMime = new PropertyValue(tiffFormat, formatMimetype, "image/tiff");

    final PropertyValue imageMagickVersion = new PropertyValue(imageMagickTool, toolVersion, "6.6.0");

    // save property values
    pdfPUID.save();
    pdfMime.save();
    tiffPUID.save();
    tiffMime.save();
    imageMagickVersion.save();

    // Async Request
    final Question question1 = new Question("?s watch:type watch-EntityType:tools", RequestTarget.ENTITY,
      Arrays.asList(tools), Arrays.asList(toolVersion), Arrays.asList(imageMagickTool), 60);
    final Map<String, String> not1config = new HashMap<String, String>();
    not1config.put("to", "lfaria@keep.pt");
    not1config.put("subject", "New tools");
    final Notification notification1 = new Notification(NotificationType.EMAIL_EVENT, not1config);
    final Trigger trigger1 = new Trigger(question1, Arrays.asList(notification1), null);

    final AsyncRequest request = new AsyncRequest(Arrays.asList(trigger1));

    question1.save();
    notification1.save();
    trigger1.save();
    request.save();

    flush();
  }

  /**
   * Hidden constructor.
   */
  private KB() {

  }

}
