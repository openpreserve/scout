package eu.scape_project.watch.adaptor.c3po.common;

import java.io.InputStream;
import java.util.Map;

public interface ProfileVersionReader {
  
  /**
   * Indicates a missing value.
   */
  public static final String MISSING_VALUE = "cp_missing_value";

  
  public void setStream(InputStream is);

  /**
   * Retrieves the collection name or identifier of the content profile.
   * 
   * @return the identifying name.
   */
  public String getCollectionName();

  /**
   * Retrieves the objects count in the collection.
   * 
   * @return a string representation of the count of objects.
   */
  public String getObjectsCount();

  /**
   * Retrieves the size of the whole collection.
   * 
   * @return a string representation of the size.
   */
  public String getCollectionSize();

  /**
   * Retrieves the size of the largest object in the collection.
   * 
   * @return a string representation of the max size.
   */
  public String getObjectsMaxSize();

  /**
   * Retrieves the size of the smallest object in the collection.
   * 
   * @return a string representation of the min size.
   */
  public String getObjectsMinSize();

  /**
   * Retrieves the average size of all objects in the collection.
   * 
   * @return the average size.
   */
  public String getObjectsAvgSize();

  public Map<String, String> getDistribution(String name);

}
