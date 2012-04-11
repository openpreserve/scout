package eu.scape_project.watch.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

import eu.scape_project.watch.dao.AsyncRequestDAO;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.dao.EntityTypeDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.dao.PropertyValueDAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyDataStructure;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;

/**
 * A utility class that holds some constants for the RDF schema and provides
 * some static utility methods.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * @author Luis Faria <lfaria@keep.pt>
 */
public final class KBUtils {

  /**
   * A default logger for this utility class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(KBUtils.class);

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
   * The XSD prefix to use in SPARQL queries.
   */
  public static final String XSD_PREFIX = "xsd:";

  /**
   * The RDF prefix to use in SPARQL queries.
   */
  public static final String RDF_PREFIX = "rdf:";

  /**
   * The WATCH prefix to use in SPARQL queries.
   */
  public static final String WATCH_PREFIX = "watch:";

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
   * A synchronous request constant.
   */
  public static final String SYNC_REQUEST = "request";

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
   * A data structure constant.
   */
  public static final String DATA_STRUCTURE_TYPE = "datastructure";

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
   * A key-value pair entry constant.
   */
  public static final String ENTRY = "entry";

  /**
   * A key-value pair item constant.
   */
  public static final String DICTIONARY_ITEM = "item";
  /**
   * A source adaptor constant.
   */
  public static final String SOURCE_ADAPTOR = "sourceadaptor";

  /**
   * The declaration of the XSD prefix to use in SPARQL queries.
   */
  public static final String XSD_PREFIX_DECL = createPrefixDecl(XSD_PREFIX, XSD_NS);

  /**
   * The declaration of the RDF prefix to use in SPARQL queries.
   */
  public static final String RDF_PREFIX_DECL = createPrefixDecl(RDF_PREFIX, RDF_NS);

  /**
   * The declaration of the WATCH prefix to use in SPARQL queries.
   */
  public static final String WATCH_PREFIX_DECL = createPrefixDecl(WATCH_PREFIX, WATCH_NS);

  /**
   * {@link EntityType} prefix to use in SPARQL queries.
   */
  public static final String WATCH_ENTITY_TYPE_PREFIX = getResourcePrefix(EntityType.class);

  /**
   * {@link EntityType} prefix declaration to use in SPARQL queries.
   */
  public static final String WATCH_ENTITY_TYPE_PREFIX_DECL = getResourcePrefixDecl(EntityType.class);
  /**
   * {@link Property} prefix to use in SPARQL queries.
   */
  public static final String WATCH_PROPERTY_PREFIX = getResourcePrefix(Property.class);

  /**
   * {@link Property} prefix declaration to use in SPARQL queries.
   */
  public static final String WATCH_PROPERTY_PREFIX_DECL = getResourcePrefixDecl(Property.class);
  /**
   * {@link Entity} prefix to use in SPARQL queries.
   */
  public static final String WATCH_ENTITY_PREFIX = getResourcePrefix(Entity.class);

  /**
   * {@link Entity} prefix declaration to use in SPARQL queries.
   */
  public static final String WATCH_ENTITY_PREFIX_DECL = getResourcePrefixDecl(Entity.class);
  /**
   * {@link PropertyValue} prefix to use in SPARQL queries.
   */
  public static final String WATCH_PROPERTY_VALUE_PREFIX = getResourcePrefix(PropertyValue.class);

  /**
   * {@link PropertyValue} prefix declaration to use in SPARQL queries.
   */
  public static final String WATCH_PROPERTY_VALUE_PREFIX_DECL = getResourcePrefixDecl(PropertyValue.class);

  /**
   * List of all defined prefixes declarations to help creating SPARQL queries.
   */
  public static final String PREFIXES_DECL = XSD_PREFIX_DECL + RDF_PREFIX_DECL + WATCH_PREFIX_DECL
    + WATCH_ENTITY_TYPE_PREFIX_DECL + WATCH_PROPERTY_PREFIX_DECL + WATCH_ENTITY_PREFIX_DECL
    + WATCH_PROPERTY_VALUE_PREFIX_DECL;

  /**
   * Debugging method that prints all statements in triple store.
   */
  public static void printStatements() {

    final StmtIterator statements = Jenabean.instance().model().listStatements();
    try {
      while (statements.hasNext()) {
        final Statement stmt = statements.next();
        final Resource s = stmt.getSubject();
        final Resource p = stmt.getPredicate();
        final RDFNode o = stmt.getObject();

        String sinfo = "";
        String pinfo = "";
        String oinfo = "";

        if (s.isURIResource()) {
          sinfo = "<" + s.getURI() + ">";
        } else if (s.isAnon()) {
          sinfo = "" + s.getId();
        }

        if (p.isURIResource()) {
          pinfo = "<" + p.getURI() + ">";
        }

        if (o.isURIResource()) {
          oinfo = "<" + o.toString() + ">";
        } else if (o.isAnon()) {
          oinfo = "" + o.toString();
        } else if (o.isLiteral()) {
          oinfo = "'" + o.asLiteral() + "'";
        }
        if (sinfo != null && pinfo != null && oinfo != null) {
          LOG.debug("({}, {}, {})", new Object[] {sinfo, pinfo, oinfo});
          System.out.println(String.format("(%1$s,%2$s,%3$s)", sinfo, pinfo, oinfo));
        }
      }
    } finally {
      statements.close();
    }
  }

  /**
   * Create a connection with the database.
   * 
   * @param datafolder
   *          The folder where the database will be kept.
   * @param testdata
   *          <code>true</code> if test data should be inserted into the
   *          database.
   */
  public static void dbConnect(final String datafolder, final boolean testdata) {
    LOG.info("DB Connect");
    final File dataFolderFile = new File(datafolder);
    try {

      if (!dataFolderFile.exists()) {
        FileUtils.forceMkdir(dataFolderFile);
      }

      final Model model = TDBFactory.createModel(datafolder);
      Jenabean.instance().bind(model);

      LOG.info("Model was created at {} and is bound to Jenabean", datafolder);

      if (testdata) {
        KBUtils.createInitialData();
      }

      // KBUtils.printStatements();

    } catch (final IOException e) {
      LOG.error("Data folder {} could not be created", e.getMessage());
    }
  }

  /**
   * Cleanly shutdown from the database, flushing the cache and closing the
   * model.
   */
  public static void dbDisconnect() {
    TDB.sync(Jenabean.instance().model());
    Jenabean.instance().model().close();
  }

  /**
   * Creates some initial data.
   */
  public static void createInitialData() {
    LOG.info("Creating initial data");

    final EntityType tools = new EntityType("tools", "Applications that read and/or write into diferent file formats");
    final EntityType formats = new EntityType("format", "File format");
    final EntityType profile = new EntityType("profile", "A content profile of a specific collection");

    final Property formatPUID = new Property(formats, "PUID", "PRONOM Id");
    final Property formatMimetype = new Property(formats, "MIME", "MIME type");

    final Property toolVersion = new Property(tools, "version", "Tool version");
    final Property inputFormat = new Property(tools, "input_format", "Supported input format",
      PropertyDataStructure.LIST);
    final Property outputFormat = new Property(tools, "output_format", "Supported output formats",
      PropertyDataStructure.LIST);
    final Property formatDistribution = new Property(profile, "format_distribution",
      "The format distribution of the content", PropertyDataStructure.DICTIONARY);

    DAO.save(formats, tools, profile);
    DAO.save(formatPUID, formatMimetype, toolVersion, inputFormat, outputFormat, formatDistribution);

    final Entity pdf17Format = new Entity(formats, "application/pdf;version=1.7");
    final Entity tiffFormat = new Entity(formats, "image/tiff;version=3.0.0");
    final Entity jpeg2000Format = new Entity(formats, "image/jp2;version=1.0.0");
    final Entity imageMagickTool = new Entity(tools, "ImageMagick_6.6.0_all_all_all");
    final Entity jpeg = new Entity(formats, "JPEG");
    final Entity jpeg2000 = new Entity(formats, "JPEG2000");
    final Entity png = new Entity(formats, "PNG");
    final Entity doc = new Entity(formats, "DOC");
    final Entity docx = new Entity(formats, "DOCX");
    final Entity bmp = new Entity(formats, "BMP");
    final Entity gif = new Entity(formats, "GIF");
    final Entity cp0 = new Entity(profile, "collection0");

    // save entities
    DAO.save(pdf17Format, tiffFormat, jpeg2000Format, imageMagickTool, jpeg, jpeg2000, png, doc, docx, bmp, gif, cp0);

    // property value construction also binds to entity
    final PropertyValue pdfPUID = new PropertyValue(pdf17Format, formatPUID, "fmt/276");
    final PropertyValue pdfMime = new PropertyValue(pdf17Format, formatMimetype, "application/pdf");
    final String tiffPUIDValue = "fmt/353";
    final PropertyValue tiffPUID = new PropertyValue(tiffFormat, formatPUID, tiffPUIDValue);
    final PropertyValue tiffMime = new PropertyValue(tiffFormat, formatMimetype, "image/tiff");
    final PropertyValue jpeg2000PUID = new PropertyValue(jpeg2000Format, formatPUID, "x-fmt/392");
    final PropertyValue jpeg2000Mime = new PropertyValue(jpeg2000Format, formatMimetype, "image/jp2");
    final PropertyValue imageMagickVersion = new PropertyValue(imageMagickTool, toolVersion, "6.6.0");

    final PropertyValue jpegMime = new PropertyValue(jpeg, formatMimetype, "image/jpeg");
    final String jpegPUIDValue = "fmt/44";
    final PropertyValue jpegPUID = new PropertyValue(jpeg, formatPUID, jpegPUIDValue);
    final PropertyValue pngPUID = new PropertyValue(png, formatPUID, "fmt/11");
    final PropertyValue pngMime = new PropertyValue(png, formatMimetype, "image/png");
    final PropertyValue docPUID = new PropertyValue(doc, formatPUID, "fmt/40");
    final PropertyValue docMime = new PropertyValue(doc, formatMimetype, "application/msword");
    final PropertyValue docxMime = new PropertyValue(docx, formatMimetype,
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    final String bmpPUIDValue = "fmt/119";
    final PropertyValue bmpPUID = new PropertyValue(bmp, formatPUID, bmpPUIDValue);
    final PropertyValue bmpMime = new PropertyValue(bmp, formatMimetype, "image/bmp");
    final String gifPUIDValue = "fmt/4";
    final PropertyValue gifPUID = new PropertyValue(gif, formatPUID, gifPUIDValue);
    final PropertyValue gifMime = new PropertyValue(gif, formatMimetype, "image/gif");

    final List<Object> values = new ArrayList<Object>(Arrays.asList(tiffPUIDValue, jpegPUIDValue, bmpPUIDValue,
      gifPUIDValue));
    final PropertyValue ifr = new PropertyValue(imageMagickTool, inputFormat, values);
    final PropertyValue ofr = new PropertyValue(imageMagickTool, outputFormat, values);

    final List<Object> distr = new ArrayList<Object>();
    distr.add(new DictionaryItem(pdf17Format.getName(), "133"));
    distr.add(new DictionaryItem(tiffFormat.getName(), "123"));
    distr.add(new DictionaryItem(jpeg2000.getName(), "42"));

    final PropertyValue distribution = new PropertyValue(cp0, formatDistribution, distr);

    final PropertyValue ifr1 = new PropertyValue(imageMagickTool, inputFormat, tiffPUIDValue);
    final PropertyValue ifr2 = new PropertyValue(imageMagickTool, inputFormat, jpegPUIDValue);
    final PropertyValue ifr3 = new PropertyValue(imageMagickTool, inputFormat, bmpPUIDValue);
    final PropertyValue ifr4 = new PropertyValue(imageMagickTool, inputFormat, gifPUIDValue);

    final PropertyValue ofr1 = new PropertyValue(imageMagickTool, outputFormat, tiffPUIDValue);
    final PropertyValue ofr2 = new PropertyValue(imageMagickTool, outputFormat, jpegPUIDValue);
    final PropertyValue ofr3 = new PropertyValue(imageMagickTool, outputFormat, bmpPUIDValue);
    final PropertyValue ofr4 = new PropertyValue(imageMagickTool, outputFormat, gifPUIDValue);

    // save property values
    DAO.save(imageMagickVersion, pdfPUID, pdfMime, tiffPUID, tiffMime, jpeg2000PUID, jpeg2000Mime, jpegPUID, jpegMime,
      pngPUID, pngMime, docPUID, docMime, docxMime, bmpPUID, bmpMime, gifPUID, gifMime, ifr, ofr, ifr1, ifr2, ifr3,
      ifr4, ofr1, ofr2, ofr3, ofr4, distribution);

    final Question question1 = new Question("?s watch:type watch-EntityType:tools", RequestTarget.ENTITY,
      Arrays.asList(tools), Arrays.asList(toolVersion), Arrays.asList(imageMagickTool), 60);
    final Map<String, String> not1config = new HashMap<String, String>();
    not1config.put("to", "lfaria@keep.pt");
    not1config.put("subject", "New tools");
    final Notification notification1 = new Notification("log", not1config);

    final Trigger trigger1 = new Trigger(question1, Arrays.asList(notification1), null);

    final AsyncRequest request = new AsyncRequest(Arrays.asList(trigger1));

    // save request
    DAO.save(request);

    TDB.sync(Jenabean.instance().model());
  }

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
   * Get a resource class prefix to be used in SPARQL queries.
   * 
   * @param <T>
   *          The resource class type.
   * 
   * @param resourceClass
   *          The resource class.
   * @return The prefix, starting with "watch-", the resource class simple name,
   *         and finishing with ":"
   */
  private static <T extends RdfBean<T>> String getResourcePrefix(final Class<T> resourceClass) {
    return "watch-" + resourceClass.getSimpleName() + ":";
  }

  /**
   * Get a resource class RDF name space, to use in SPARQL queries.
   * 
   * @param <T>
   *          The resource class type.
   * @param resourceClass
   *          The resource class.
   * @return The resource namespace, that start with the watch namespace
   *         {@link #WATCH_NS}.
   */
  private static <T extends RdfBean<T>> String getResourceNamespace(final Class<T> resourceClass) {
    return WATCH_NS + resourceClass.getSimpleName() + "/";
  }

  /**
   * Get the resource class prefix declaration to use in SPARQL queries.
   * 
   * @param <T>
   *          The resource class type.
   * @param resourceClass
   *          The resource class.
   * @return The prefix declaration.
   */
  private static <T extends RdfBean<T>> String getResourcePrefixDecl(final Class<T> resourceClass) {
    return createPrefixDecl(getResourcePrefix(resourceClass), getResourceNamespace(resourceClass));
  }

  /*
   * hidden constructor.
   */
  /**
   * Utility classes do not have public constructors.
   */
  private KBUtils() {

  }

}
