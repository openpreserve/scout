package eu.scape_project.watch.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDB;

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

public class KBUtils {
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

  public static void printStatements() {

    StmtIterator statements = Jenabean.instance().model().listStatements();
    try {
      while (statements.hasNext()) {
        Statement stmt = statements.next();
        Resource s = stmt.getSubject();
        Resource p = stmt.getPredicate();
        RDFNode o = stmt.getObject();

        String sinfo = "";
        String pinfo = "";
        String oinfo = "";

        if (s.isURIResource()) {
          sinfo = "<" + s.getURI() + ">";
        } else if (s.isAnon()) {
          sinfo = "" + s.getId();
        }

        if (p.isURIResource())
          pinfo = "<" + p.getURI() + ">";

        if (o.isURIResource()) {
          oinfo = "<" + o.toString() + ">";
        } else if (o.isAnon()) {
          oinfo = "" + o.toString();
        } else if (o.isLiteral()) {
          oinfo = "'" + o.asLiteral() + "'";
        }
        LOG.debug("({}, {}, {})", new Object[] { sinfo, pinfo, oinfo });
      }
    } catch (Throwable e) {
      LOG.info(e.getMessage());
    } finally {
      statements.close();
    }
  }

  /**
   * Creates some initial data.
   */
  public static void createInitialData() {
    LOG.info("Creating initial data");
    final EntityType formats = new EntityType("format", "File format");
    final Property formatPUID = new Property(formats, "PUID", "PRONOM Id");
    final Property formatMimetype = new Property(formats, "MIME", "MIME type");

    formats.save();
    formatPUID.save();
    formatMimetype.save();

    final EntityType tools = new EntityType("tools", "Applications that read and/or write into diferent file formats");
    final Property toolVersion = new Property(tools, "version", "Tool version");

    Property inputFormat = new Property(tools, "input_format", "Supported input format");
    Property outputFormat = new Property(tools, "output_format", "Supported output formats");
    
    tools.save();
    toolVersion.save();
    inputFormat.save();
    outputFormat.save();

    final Entity pdf17Format = new Entity(formats, "application/pdf;version=1.7");
    final Entity tiffFormat = new Entity(formats, "image/tiff;version=3.0.0");
    final Entity jpeg2000Format = new Entity(formats, "image/jp2;version=1.0.0");
    final Entity imageMagickTool = new Entity(tools, "ImageMagick_6.6.0_all_all_all");

    Entity jpeg = new Entity(formats, "JPEG");
    Entity jpeg2000 = new Entity(formats, "JPEG2000");
    Entity png = new Entity(formats,"PNG");
    Entity doc = new Entity(formats, "DOC");
    Entity docx = new Entity(formats, "DOCX");
    Entity bmp = new Entity(formats, "BMP");
    Entity gif = new Entity(formats, "GIF");
    
    
    // save entities
    pdf17Format.save();
    tiffFormat.save();
    jpeg2000Format.save();
    imageMagickTool.save();
    jpeg.save();
    jpeg2000.save();
    png.save();
    doc.save();
    docx.save();
    bmp.save();
    gif.save();
    

    // property value construction also binds to entity
    final PropertyValue pdfPUID = new PropertyValue(pdf17Format, formatPUID, "fmt/276");
    final PropertyValue pdfMime = new PropertyValue(pdf17Format, formatMimetype, "application/pdf");

    final PropertyValue tiffPUID = new PropertyValue(tiffFormat, formatPUID, "fmt/353");
    final PropertyValue tiffMime = new PropertyValue(tiffFormat, formatMimetype, "image/tiff");

    final PropertyValue jpeg2000PUID = new PropertyValue(jpeg2000Format, formatPUID, "x-fmt/392");
    final PropertyValue jpeg2000Mime = new PropertyValue(jpeg2000Format, formatMimetype, "image/jp2");

    final PropertyValue imageMagickVersion = new PropertyValue(imageMagickTool, toolVersion, "6.6.0");

    imageMagickVersion.save();

    PropertyValue jpegMime = new PropertyValue(jpeg,formatMimetype,"image/jpeg");
    PropertyValue jpegPUID = new PropertyValue(jpeg, formatPUID, "fmt/44");
    
    PropertyValue pngPUID = new PropertyValue(png, formatPUID, "fmt/11");
    PropertyValue pngMime = new PropertyValue(png,formatMimetype,"image/png");
    
    PropertyValue docPUID = new PropertyValue(doc, formatPUID, "fmt/40");
    PropertyValue docMime = new PropertyValue(doc,formatMimetype,"application/msword");
    
    PropertyValue docxMime = new PropertyValue(docx,formatMimetype, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    
    PropertyValue bmpPUID = new PropertyValue(bmp, formatPUID, "fmt/119");
    PropertyValue bmpMime = new PropertyValue(bmp,formatMimetype,"image/bmp");
    
    PropertyValue gifPUID = new PropertyValue(gif, formatPUID, "fmt/4");
    PropertyValue gifMime = new PropertyValue(gif,formatMimetype,"image/gif");
    
    PropertyValue ifr1 = new PropertyValue(imageMagickTool, inputFormat, "fmt/353");
    PropertyValue ifr2 = new PropertyValue(imageMagickTool, inputFormat, "fmt/44");
    PropertyValue ifr3 = new PropertyValue(imageMagickTool, inputFormat, "fmt/119");
    PropertyValue ifr4 = new PropertyValue(imageMagickTool, inputFormat, "fmt/4");
    
    
    PropertyValue ofr1 = new PropertyValue(imageMagickTool, outputFormat, "fmt/353");
    PropertyValue ofr2 = new PropertyValue(imageMagickTool, outputFormat, "fmt/44");
    PropertyValue ofr3 = new PropertyValue(imageMagickTool, outputFormat, "fmt/119");
    PropertyValue ofr4 = new PropertyValue(imageMagickTool, outputFormat, "fmt/4");
    
    // save property values
    pdfPUID.save();
    pdfMime.save();
    tiffPUID.save();
    tiffMime.save();
    jpeg2000PUID.save();
    jpeg2000Mime.save();

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

    imageMagickVersion.save();
    jpegPUID.save();
    jpegMime.save();
    jpeg2000PUID.save();
    jpeg2000Mime.save();
    pngPUID.save();
    pngMime.save();
    docPUID.save();
    docMime.save();
    docxMime.save();
    bmpPUID.save();
    bmpMime.save();
    gifPUID.save();
    gifMime.save();
    
    
    
    ifr1.save();
    ifr2.save();
    ifr3.save();
    ifr4.save();
    
    ofr1.save();
    ofr2.save();
    ofr3.save();
    ofr4.save();
    
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
  static String createPrefixDecl(final String prefix, final String namescape) {
    return "PREFIX " + prefix + " <" + namescape + ">\n";
  }

  static <T extends RdfBean<T>> String getResourcePrefix(Class<T> resourceClass) {
    return "watch-" + resourceClass.getSimpleName() + ":";
  }

  static <T extends RdfBean<T>> String getResourceNamespace(Class<T> resourceClass) {
    return WATCH_NS + resourceClass.getSimpleName() + "/";
  }

  static <T extends RdfBean<T>> String getResourcePrefixDecl(Class<T> resourceClass) {
    return createPrefixDecl(getResourcePrefix(resourceClass), getResourceNamespace(resourceClass));
  }

}
