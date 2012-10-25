package eu.scape_project.watch.adaptor.c3po.common;

/**
 * A utility class holding some shared constants.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public final class C3POConstants {

  // ############ configuration of the adaptor #################
  /**
   * The configuration parameter for the c3po endpoint. It should be a correct
   * http(s) web address (with port if needed), or 'dummy' if the source should
   * be simulated.
   */
  public static final String ENDPOINT_CNF = "c3po.endpoint";

  /**
   * The default value of the {@link C3POConstants#ENDPOINT_CNF} configuration
   * parameter.
   */
  public static final String ENDPOINT_DEFAULT = "dummy";

  /**
   * A human readable description of the {@link C3POConstants#ENDPOINT_CNF}
   * configuration parameter.
   */
  public static final String ENDPOINT_DESC = "The url endpoint for the c3po profiler source";

  // ############ supported properties #################
  /**
   * The collection name identifier property.
   */
  public static final String CP_COLLECTION_IDENTIFIER = "Collection name";

  /**
   * The collection overall size property.
   */
  public static final String CP_COLLECTION_SIZE = "Collection size";

  /**
   * The count of the objects property.
   */
  public static final String CP_OBJECTS_COUNT = "Objects count";

  /**
   * The avg size of an object within the collection property.
   */
  public static final String CP_OBJECTS_AVG_SIZE = "Objects avg size";

  /**
   * The size of the smallest object property (in bytes).
   */
  public static final String CP_OBJECTS_MIN_SIZE = "Objects min size";

  /**
   * The size of the largest object property (in bytes).
   */
  public static final String CP_OBJECTS_MAX_SIZE = "Objects max size";

  /**
   * The standard deviation of the size of objects property (in bytes).
   */
  public static final String CP_OBJECTS_SD_SIZE = "Objects sd size";

  /**
   * The statistical mode of the formats within the collection property.
   */
  public static final String CP_FORMAT_MODE = "Format mode";

  /**
   * The format distribution property.
   */
  public static final String CP_DISTRIBUTION = "%s distribution";

  /**
   * The statistical mode of the PRONOM identifiers within the collection
   * property.
   */
  public static final String CP_PUID_MODE = "cp.puid.mode";

  /**
   * The distribution of PRONOM identifiers within the collection property.
   */
  public static final String CP_PUID_DISTRIBUTION = "cp.puid.distribution";

  /**
   * The statistical mode of the mime types property.
   */
  public static final String CP_MIMETYPE_MODE = "cp.mimetype.mode";

  /**
   * The distribution of the mime types property.
   */
  public static final String CP_MIMETYPE_DISTRIBUTION = "cp.mimetype.distribution";

  // ############ misc #################

  /**
   * The name of the collection profile Entity.
   */
  public static final String CP_NAME = "content_profile";
  
  /**
   * The human readable description of the collection profile Entity.
   */
  public static final String CP_DESCRIPTION = "Represents a content profile";

  /**
   * Utility classes have only hidden constructors.
   */
  private C3POConstants() {

  }
}
