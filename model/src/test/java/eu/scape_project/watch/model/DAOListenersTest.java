package eu.scape_project.watch.model;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.utils.KBUtils;

public class DAOListenersTest {
  
  private static int count = 0;
  
  /**
   * A temporary directory to hold the data.
   */
  private static final String DATA_TEMP_DIR = "/tmp/watch";

  /**
   * Initialize the data folder.
   */
  @BeforeClass
  public static void beforeClass() {
    final String datafolder = DATA_TEMP_DIR;
    final boolean initdata = false;
    KBUtils.dbConnect(datafolder, initdata);
  }

  /**
   * Cleanup the data folder.
   */
  @AfterClass
  public static void afterClass() {
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(new File(DATA_TEMP_DIR));
  }
  
  @Test
  public void shouldRegisterAndCallListenerTest() throws Exception {
    DAO.addDOListener(EntityType.class, new EntityTypeListener());
    
    DAO.ENTITY_TYPE.save(new EntityType("test", "test entity type"));
    Assert.assertEquals(1, count);
    
    DAO.clearDOListeners(); // remove this line to fail next test!
  }
  
  @Test
  public void shouldNotHaveListenerInstalled() throws Exception {
    DAO.ENTITY_TYPE.save(new EntityType("test", "test entity type"));
    Assert.assertEquals(1, count);
  }
  
  
  
  private class EntityTypeListener implements DOListener<EntityType> {

    @Override
    public void onUpdated(EntityType object) {
      DAOListenersTest.count++;
    }

    @Override
    public void onRemoved(EntityType object) {
      
    }
    
  }
}
