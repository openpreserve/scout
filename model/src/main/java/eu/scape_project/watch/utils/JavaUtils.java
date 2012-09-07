package eu.scape_project.watch.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Generic utilities.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class JavaUtils {

  /**
   * Utility classes should not have instances.
   */
  private JavaUtils() {

  }

  /**
   * Create a temporary directory.
   * 
   * XXX replace by Files method in JDK 7.
   * 
   * @return The temporary directory.
   * @throws IOException
   *           Error creating temporary directory.
   */
  public static File createTempDirectory() throws IOException {
    final File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

    if (!(temp.delete())) {
      throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
    }

    if (!(temp.mkdir())) {
      throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
    }

    return temp;
  }

  /**
   * Null safe toString of lists.
   * 
   * @param list
   *          The list to print to string.
   * @return The string.
   */
  public static String toString(final List<?> list) {
    if (list != null) {
      return Arrays.toString(list.toArray());
    } else {
      return "null";
    }
  }

}
