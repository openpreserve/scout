package eu.scape_project.watch.adaptor.c3po.client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import eu.scape_project.watch.adaptor.c3po.common.ProfileVersionReader;

/**
 * A simple client interface to the c3po content profiler tool. It allows the
 * client application to query c3po and fetch content profiles.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface C3POClientInterface {

  /**
   * Fetches the current content profile identifiers stored in the c3po
   * instance.
   * 
   * @return a list of collection identifiers
   */
  List<String> getCollectionIdentifiers();

  /**
   * A method that gets a collection profile for a given collection identifier
   * and a map of parameters.
   * 
   * @param identifier
   *          the collection identifier.
   * @param parameters
   *          the parameters.
   * @return an InputStream of the collection profile or null.
   */
  InputStream getCollectionProfile(String identifier, Map<String, String> parameters);

  /**
   * Retrieves the correct {@link ProfileVersionReader} for this source.
   * 
   * @return the reader.
   */
  ProfileVersionReader getReader();

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
  @Deprecated
  String submitCollectionProfileJob(String identifier, List<String> properties);

  /**
   * Retrieves an input stream with the results of the job for the given uuid,
   * or null if the job is not yet finished or unknown.
   * 
   * @param uuid
   *          the job id.
   * @return the InputStream holding the profile.
   */
  @Deprecated
  InputStream pollJobResult(String uuid);
}
