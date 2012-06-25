package eu.scape_project.watch.adaptor.pronom;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * Tests the {@link PronomAdaptor}.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PronomAdaptorTest {

  /**
   * The object under test.
   */
  private PronomAdaptor adaptor;

  /**
   * Creates the adaptor and initalizes it.
   */
  @Before
  public void setup() {
    this.adaptor = new PronomAdaptor();
    try {
      this.adaptor.init();
    } catch (PluginException e) {
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
  }

  /*
   * WARNING: scrapes the whole source. ok for now (as it is neglectably small)
   * but should be changed later. //TODO
   */
  /**
   * Executes the adaptor and checks that the response is not empty.
   * 
   * @throws Exception
   */
  @Test @Ignore
  public void onExecute() throws Exception {
    ResultInterface execute = this.adaptor.execute();
    Assert.assertNotNull(execute);
    Assert.assertFalse(execute.getPropertyValues().isEmpty());
  }
}
