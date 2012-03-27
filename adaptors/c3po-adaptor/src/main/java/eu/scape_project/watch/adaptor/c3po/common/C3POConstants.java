package eu.scape_project.watch.adaptor.c3po.common;

public class C3POConstants {

  // ############ configuration of the adaptor #################
  public static final String ENDPOINT_CNF = "c3po.endpoint";
  public static final String ENDPOINT_DEFAULT = "dummy";
  public static final String ENDPOINT_DESC = "The url endpoint for the c3po profiler source";

  // ############ supported properties #################
  public static final String CP_COLLECTION_IDENTIFIER = "cp.collection.name";
  public static final String CP_COLLECTION_SIZE = "cp.collection.size";

  public static final String CP_OBJECTS_COUNT = "cp.objects.count";
  public static final String CP_OBJECTS_AVG_SIZE = "cp.objects.size.avg";
  public static final String CP_OBJECTS_MIN_SIZE = "cp.objects.size.min";
  public static final String CP_OBJECTS_MAX_SIZE = "cp.objects.size.max";
  public static final String CP_OBJECTS_SD_SIZE = "cp.objects.size.sd";

  public static final String CP_FORMAT_MODE = "cp.format.mode";
  public static final String CP_FORMAT_DISTRIBUTION = "cp.format.distribution";

  public static final String CP_PUID_MODE = "cp.puid.mode";
  public static final String CP_PUID_DISTRIBUTION = "cp.puid.distribution";

  public static final String CP_MIMETYPE_MODE = "cp.mimetype.mode";
  public static final String CP_MIMETYPE_DISTRIBUTION = "cp.mimetype.distribution";

  // ############ misc #################

  public static final String CP_NAME = "collection_profile";
  public static final String CP_DESCRIPTION = "A simple collection profile";
}
