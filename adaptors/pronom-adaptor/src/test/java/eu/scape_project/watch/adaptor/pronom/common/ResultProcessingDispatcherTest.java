package eu.scape_project.watch.adaptor.pronom.common;

import java.io.File;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultProcessingDispatcherTest {

  private static final Logger LOG = LoggerFactory.getLogger(ResultProcessingDispatcherTest.class);

  private static final String CACHE_FILE_PATH = "src/test/resources/cache.txt";

  @AfterClass
  public static void tearDown() {
    deleteFile();
  }

  @Test
  public void shouldTestDispatching() {
    final ResultProcessingDispatcher dispatcher = new ResultProcessingDispatcher(CACHE_FILE_PATH);
    final String test = "42";

    boolean process = dispatcher.process(test);
    Assert.assertTrue(process);

    process = dispatcher.process(test);
    Assert.assertFalse(process);

    deleteFile();
  }

  @Test
  public void shouldTestFileReading() throws Exception {
    ResultProcessingDispatcher dispatcher = new ResultProcessingDispatcher(CACHE_FILE_PATH);
    final String test = "42";

    boolean process = dispatcher.process(test);
    Assert.assertTrue(process);

    dispatcher = new ResultProcessingDispatcher(CACHE_FILE_PATH);
    process = dispatcher.process(test);
    Assert.assertFalse(process);

    deleteFile();
  }

  private static void deleteFile() {
    LOG.debug("Cleaning up test cache file.");
    final File file = new File(CACHE_FILE_PATH);
    file.delete();
  }
}
