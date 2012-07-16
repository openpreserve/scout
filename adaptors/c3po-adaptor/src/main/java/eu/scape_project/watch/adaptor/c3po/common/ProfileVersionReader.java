package eu.scape_project.watch.adaptor.c3po.common;

import java.io.InputStream;
import java.util.Map;

/**
 * An interface for the content profile parser versions.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface ProfileVersionReader {

  /**
   * Indicates a missing value.
   */
  String MISSING_VALUE = "cp_missing_value";

  /**
   * Sets the input stream of the content profile document.
   * 
   * @param is
   *          the input stream.
   */
  void setStream(InputStream is);

  /**
   * Retrieves the collection name or identifier of the content profile.
   * 
   * @return the identifying name.
   */
  String getCollectionName();

  /**
   * Retrieves the objects count in the collection.
   * 
   * @return a string representation of the count of objects.
   */
  String getObjectsCount();

  /**
   * Retrieves the size of the whole collection.
   * 
   * @return a string representation of the size.
   */
  String getCollectionSize();

  /**
   * Retrieves the size of the largest object in the collection.
   * 
   * @return a string representation of the max size.
   */
  String getObjectsMaxSize();

  /**
   * Retrieves the size of the smallest object in the collection.
   * 
   * @return a string representation of the min size.
   */
  String getObjectsMinSize();

  /**
   * Retrieves the average size of all objects in the collection.
   * 
   * @return the average size.
   */
  String getObjectsAvgSize();

  /**
   * Retrieves the distribution of the passed property.
   * 
   * @param name
   *          the name of the property.
   * @return the distribution map.
   */
  Map<String, String> getDistribution(String name);

}
