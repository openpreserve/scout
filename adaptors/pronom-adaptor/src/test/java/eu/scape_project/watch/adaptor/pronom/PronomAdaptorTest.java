package eu.scape_project.watch.adaptor.pronom;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * Tests the {@link PronomAdaptor}.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PronomAdaptorTest {

  private static final Logger LOG = LoggerFactory.getLogger(PronomAdaptorTest.class);

  /**
   * The object under test.
   */
  private PronomAdaptor adaptor;

  private File cache;

  /**
   * Creates the adaptor and initalizes it.
   */
  @Before
  public void setup() {
    this.createCacheFile();
    this.adaptor = new PronomAdaptor();
    try {
      this.adaptor.init();

      final Map<String, String> config = new HashMap<String, String>();
      config.put("pronom.cache.file.path", this.cache.getAbsolutePath());
      config.put("pronom.batch.size", "5");
      this.adaptor.setParameterValues(config);
    } catch (PluginException e) {
      e.printStackTrace();
    } catch (InvalidParameterException e) {
      e.printStackTrace();
    }

  }

  /**
   * Shuts down the adaptor.
   */
  @After
  public void tearDown() {
    try {
      this.adaptor.shutdown();
    } catch (PluginException e) {
      e.printStackTrace();
    }
    this.removeCacheFile();
  }

  /**
   * Executes the adaptor and checks that the response is not empty.
   * 
   * @throws Exception
   */
  @Test
  public void onExecute() throws Exception {
    final boolean hasNext = this.adaptor.hasNext();
    Assert.assertTrue(hasNext);

    final ResultInterface result = this.adaptor.next();
    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getValue());
    Assert.assertNotNull(result.getProperty());
    Assert.assertNotNull(result.getEntity());
  }

  private void createCacheFile() {
    cache = new File("src/test/resources/cache.txt");
    try {
      cache.createNewFile();
    } catch (IOException e) {
      LOG.error("Could not create cache file.");
    }
  }
  
  private void removeCacheFile() {
    if (this.cache != null && this.cache.exists()) {
      this.cache.delete();
    }
  }
}
