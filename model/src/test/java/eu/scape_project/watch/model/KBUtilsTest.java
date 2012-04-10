package eu.scape_project.watch.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.scape_project.watch.utils.KBUtils;

import org.apache.commons.io.FileUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

import thewebsemantic.binding.Jenabean;

/**
 * 
 * Testing {@link KBUtils}.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class KBUtilsTest {

  /**
   * Temporary data folder.
   */
  private static File dataFolder;

  /**
   * Create temporary data folder.
   * 
   * @return A created directory folder inside the temporary folder.
   * @throws IOException
   *           Thrown when could not create the directory.
   */
  public static File createTempDirectory() throws IOException {
    final File temp;

    temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

    if (!(temp.delete())) {
      throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
    }

    if (!(temp.mkdir())) {
      throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
    }

    return temp;
  }

  /**
   * Create data folder.
   * 
   * @throws IOException
   *           Could not create the data folder.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws IOException {
    dataFolder = createTempDirectory();
  }

  /**
   * Delete temporary data folder.
   * 
   * @throws IOException
   *           Could not delete the data folder.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException {
    FileUtils.forceDelete(dataFolder);
  }

  /**
   * Test {@link KBUtils} methods.
   */
  @Test
  public void testKBUtil() {
    KBUtils.dbConnect(dataFolder.getPath(), true);

    final StmtIterator statements = Jenabean.instance().model().listStatements();

    Assert.assertTrue(statements.hasNext());

    final ByteArrayOutputStream outb = new ByteArrayOutputStream();
    final PrintStream outp = new PrintStream(outb);
    System.setOut(outp);
    KBUtils.printStatements();

    Assert.assertTrue(outb.size() > 0);

    KBUtils.dbDisconnect();

  }

}
