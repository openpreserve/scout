package eu.scape_project.watch.adaptor.c3po.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * A content profile parser. Currently it wraps a {@link ProfileVersionReader}
 * and uses its internal implementation. The profile version reader may be
 * specific to the source. E.g. the dummy implementation relies on a specific
 * format to read but future production c3po version can have their own format version.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class C3POProfileReader {

  private ProfileVersionReader reader;

  /**
   * A file based constructor for test purposes. Used mainly in tests and the
   * dummy adaptor.
   * 
   * @param f
   *          the file that has the content profile.
   * @throws FileNotFoundException
   *           if no file was found.
   */
  public C3POProfileReader(final ProfileVersionReader reader, final File f) throws FileNotFoundException {
    this(reader, new FileInputStream(f));
  }

  /**
   * Creates the reader based on the xml document representation stored in the
   * input stream.
   * 
   * @param is
   *          the input stream of the content profile document.
   */
  public C3POProfileReader(final ProfileVersionReader reader, final InputStream is) {
    this.reader = reader;
    this.reader.setStream(is);
  }

  /**
   * Retrieves the collection name or identifier of the content profile.
   * 
   * @return the identifying name.
   */
  public String getCollectionName() {
    return this.reader.getCollectionName();
  }

  /**
   * Retrieves the objects count in the collection.
   * 
   * @return a string representation of the count of objects.
   */
  public String getObjectsCount() {
    return this.reader.getObjectsCount();
  }

  /**
   * Retrieves the size of the whole collection.
   * 
   * @return a string representation of the size.
   */
  public String getCollectionSize() {
    return this.reader.getCollectionSize();
  }

  /**
   * Retrieves the size of the largest object in the collection.
   * 
   * @return a string representation of the max size.
   */
  public String getObjectsMaxSize() {
    return this.reader.getObjectsMaxSize();
  }

  /**
   * Retrieves the size of the smallest object in the collection.
   * 
   * @return a string representation of the min size.
   */
  public String getObjectsMinSize() {
    return this.reader.getObjectsMinSize();
  }

  /**
   * Retrieves the average size of all objects in the collection.
   * 
   * @return the average size.
   */
  public String getObjectsAvgSize() {
    return this.reader.getObjectsAvgSize();
  }

  public Map<String, String> getDistribution(String name) {
    return this.reader.getDistribution(name);
  }

}
