package eu.scape_project.watch.adaptor;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.plugin.PluginManager;
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
  }

  @Test
  @Ignore
  public void shouldLoadAdaptors() throws Exception {
    final AdaptorManager manager = new AdaptorManager();
    final SourceAdaptor adaptor = manager.getSourceAdaptor("c3po-0.0.3");
    Assert.assertNotNull(adaptor);

  }

  @Test
  @Ignore
  public void shouldGetAdaptorPluginInterface() throws Exception {
    final AdaptorManager manager = new AdaptorManager();
    final SourceAdaptor adaptor = manager.getSourceAdaptor("c3po-0.0.3");
    Assert.assertNotNull(adaptor);

    final AdaptorPluginInterface plugin = manager.getAdaptorInstance(adaptor.getInstance());
    Assert.assertNull(plugin);
  }
}
