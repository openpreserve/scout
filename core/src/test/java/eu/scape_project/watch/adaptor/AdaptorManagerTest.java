package eu.scape_project.watch.adaptor;

import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.KBUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import static org.junit.Assert.*;

public class AdaptorManagerTest {
  
  @Before
  public void setup() {
    final String datafolder = System.getProperty("java.io.tmpdir");
    final boolean insertData = true;
    KBUtils.dbConnect(datafolder, insertData);
    PluginManager.getDefaultPluginManager().getConfig().override(ConfigUtils.DEFAULT_CONFIG);
    PluginManager.getDefaultPluginManager().setup();
  }
  
  @After
  public void teardown() {
    KBUtils.dbDisconnect();
  }

  @Test
  public void shouldLoadAdaptors() throws Exception {
    final AdaptorManager manager = new AdaptorManager();
    final SourceAdaptor adaptor = manager.getSourceAdaptor("c3po-0.0.3");
    Assert.assertNotNull(adaptor);
    
  }
  
  @Test
  public void shouldGetAdaptorPluginInterface() throws Exception {
    final AdaptorManager manager = new AdaptorManager();
    final SourceAdaptor adaptor = manager.getSourceAdaptor("c3po-0.0.3");
    Assert.assertNotNull(adaptor);
    
    AdaptorPluginInterface plugin = manager.getAdaptorInstance(adaptor.getInstance());
    Assert.assertNull(plugin);
  }
}
