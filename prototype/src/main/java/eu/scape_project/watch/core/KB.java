package eu.scape_project.watch.core;

import java.io.File;
import java.io.IOException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

import eu.scape_project.watch.core.common.ConfigUtils;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.binding.Jenabean;

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
   * The default rdf syntax namespaces.
   */
  public static final String RDFS_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

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

    flush();
  }

  /**
   * Hidden constructor.
   */
  private KB() {

  }

}
