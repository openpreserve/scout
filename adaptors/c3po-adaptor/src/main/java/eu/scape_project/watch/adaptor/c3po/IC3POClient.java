package eu.scape_project.watch.adaptor.c3po;

import java.io.InputStream;
import java.util.List;

/**
 * A simple client interface to the c3po content profiler tool. It allows the
 * client application to query c3po and fetch content profiles.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface IC3POClient {

  /**
   * Fetches the current content profile identifiers stored in the c3po
   * instance.
   * 
   * @return a list of collection identifiers
   */
  List<String> getCollectionIdentifiers();

  /*
   * the list properties should probably be changed to some kind of map-like
   * config object that can hold many parameters and options submitted to the
   * client.
   */
  /**
   * Submits a new profile creation job to c3po.
   * 
   * @param identifier
   *          the collection identifier
   * @param properties
   *          the properties that are of interest
   * @return a string representing the uuid of the job or null if 404.
   */
  String submitCollectionProfileJob(String identifier, List<String> properties);

  /**
   * Retrieves an input stream with the results of the job for the given uuid,
   * or null if the job is not yet finished or unknown.
   * 
   * @param uuid
   *          the job id.
   * @return the InputStream holding the profile.
   */
  InputStream pollJobResult(String uuid);
}
