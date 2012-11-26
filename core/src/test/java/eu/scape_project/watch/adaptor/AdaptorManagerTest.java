package eu.scape_project.watch.adaptor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.plugin.PluginManagerTest;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;

public class AdaptorManagerTest {

  /**
   * Folder where data of the knowledge base is kept.
   */
  private File dataFolder;

  @Before
  public void setup() throws IOException {
    dataFolder = JavaUtils.createTempDirectory();

    final boolean insertData = false;
    KBUtils.dbConnect(dataFolder.getPath(), insertData);
    PluginManager.getDefaultPluginManager().getConfig().override(ConfigUtils.DEFAULT_CONFIG);
    PluginManager.getDefaultPluginManager().setup();
  }

  @After
  public void teardown() {
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataFolder);
    PluginManagerTest.deleteFolder(new File("src/test/resources/plugins/adaptors"));
  }

  @Test
  public void shouldLoadAdaptors() throws Exception {
    final AdaptorManager manager = new AdaptorManager();
    
    SourceAdaptor adaptor = manager.getSourceAdaptor("nonexisting");
    Assert.assertNull(adaptor);
    
    PluginManagerTest.copyTestJar("testadaptor.jar");
    PluginManager.getDefaultPluginManager().reScan();
    
    Thread.sleep(1000);
    
    final List<PluginInfo> pluginInfos = PluginManager.getDefaultPluginManager().getPluginInfo("TestAdaptor");
    Assert.assertFalse(pluginInfos.isEmpty());
    Assert.assertTrue(pluginInfos.size() == 1);
    
    final PluginInfo pluginInfo = pluginInfos.get(0);
    adaptor = manager.createAdaptor(pluginInfo, "test1", null, null);
    
    SourceAdaptor sourceAdaptor = manager.getSourceAdaptor("test1");
    
    Assert.assertEquals(adaptor, sourceAdaptor);
    

  }
  
  @Test
  public void shouldGetCorrectPluginInstance() throws Exception {
    final AdaptorManager manager = new AdaptorManager();
    PluginManagerTest.copyTestJar("testadaptor.jar");
    PluginManager.getDefaultPluginManager().reScan();
    
    Thread.sleep(1000);
    
    List<PluginInfo> pluginInfos = PluginManager.getDefaultPluginManager().getPluginInfo("TestAdaptor");
    Assert.assertFalse(pluginInfos.isEmpty());
    Assert.assertTrue(pluginInfos.size() == 1);
    
    PluginInfo pluginInfo = pluginInfos.get(0);
    SourceAdaptor adaptor1 = manager.createAdaptor(pluginInfo, "test1", null, null);
    SourceAdaptor adaptor2 = manager.createAdaptor(pluginInfo, "test2", null, null);
    
    AdaptorPluginInterface plugin1 = manager.createAdaptorInstance("test1");
    AdaptorPluginInterface plugin2 = manager.createAdaptorInstance("test2");
    AdaptorPluginInterface plugin3 = manager.createAdaptorInstance("nonexisting");
    
    Assert.assertNull(plugin3);
    
    Assert.assertNotSame(plugin1, plugin2);
    Assert.assertNotSame(plugin1.hashCode(), plugin2.hashCode());
    
    SourceAdaptor sourceAdaptor1 = manager.getSourceAdaptor(plugin1);
    SourceAdaptor sourceAdaptor2 = manager.getSourceAdaptor(plugin2);
    
    Assert.assertEquals(adaptor1.getInstance(), sourceAdaptor1.getInstance());
    Assert.assertEquals(adaptor2.getInstance(), sourceAdaptor2.getInstance());
    
  }
}
