package eu.scape_project.watch.core.listener;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

import eu.scape_project.watch.core.KBUtils;
import eu.scape_project.watch.core.common.ConfigUtils;

public class ApplicationStartupListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartupListener.class);

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    LOG.info("Destroying Watch Application context");
    destroy();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    LOG.info("Starting Watch Application");
    init();
  }

  /**
   * Opens up the tdb connection and looks for the current configuration and
   * initializes the data folder if needed. If the knowledgebase is empty some
   * test data is added.
   * 
   */
  private void init() {
    final ConfigUtils conf = new ConfigUtils();
    String datafolder = conf.getStringProperty(ConfigUtils.KB_DATA_FOLDER_KEY);
    boolean initdata = conf.getBooleanProperty(ConfigUtils.KB_INSERT_TEST_DATA);
    
    final File dataFolderFile = new File(datafolder);
    try {
      
      if (!dataFolderFile.exists()) {
        FileUtils.forceMkdir(dataFolderFile);
        initdata = true; //init first time.. later we should remove this line
      }

      Model model = TDBFactory.createModel(datafolder);
      Jenabean.instance().bind(model);

      LOG.info("Model was created at {} and is bound to Jenabean", datafolder);

      if (initdata) {
        KBUtils.createInitialData();
      }

      KBUtils.printStatements();

    } catch (final IOException e) {
      LOG.error("Data folder {} could not be created", e.getMessage());
    }
  }
  
  private void destroy() {
    TDB.sync(Jenabean.instance().model());
    Jenabean.instance().model().close();
  }
  

}
