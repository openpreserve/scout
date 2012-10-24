package eu.scape_project.watch.model;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;

public class SupportLongInKBTest {

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(SupportLongInKBTest.class);

  /**
   * A temporary directory to hold the data.
   */
  private File dataTempir;

  /**
   * Initialize the data folder.
   * 
   * @throws IOException
   *           Error creating temporary data folder
   */
  @Before
  public void before() throws IOException {
    dataTempir = JavaUtils.createTempDirectory();
    KBUtils.dbConnect(dataTempir.getPath(), false);
  }

  /**
   * Cleanup the data folder.
   */
  @After
  public void after() {
    LOG.info("Deleting data folder at " + dataTempir);
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempir);
  }

  /**
   * Testing Long support.
   */
  @Test
  public void testLongSupport() {

    final String id = "test";
    final long value = 123L;
    final LongTestClass longTest = new LongTestClass(id, value);

    longTest.save();

    final LongTestClass long1 = Jenabean.load(LongTestClass.class, id);

    Assert.assertEquals(value, long1.getLongField());
  }

  /**
   * Testing Integer support.
   */
  @Test
  public void testIntegerSupport() {

    final String id = "test";
    final Integer value = 123;
    final IntegerTestClass integerTest = new IntegerTestClass(id, value);

    integerTest.save();

    final IntegerTestClass integer1 = Jenabean.load(IntegerTestClass.class, id);

    Assert.assertEquals(value, integer1.getIntegerField());
  }
}
