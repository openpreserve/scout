package eu.scape_project.watch.policy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.scape_project.watch.domain.Objective;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;

public class PolicyModelTest {


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
    //LOG.info("Deleting data folder at " + dataTempir);
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempir);
  }
  
  @Test
  public void shouldLoadPolicies() throws Exception {
    PolicyModel policyModel = new PolicyModel();
    
    boolean loaded = policyModel.loadPolicies("src/test/resources/policies/giberrish.txt");
    Assert.assertFalse(loaded);
    
    loaded = policyModel.loadPolicies("src/test/resources/policies/bl_policies.rdf");
    Assert.assertTrue(loaded);
    
  }
  
  @Test
  public void shouldGetAllObjectives() throws Exception {
    PolicyModel policyModel = new PolicyModel();
    boolean loaded = policyModel.loadPolicies("src/test/resources/policies/bl_policies.rdf");
    
    Assert.assertTrue(loaded);
    
    List<Objective> allObjectives = policyModel.listAllObjectives();
    
    Assert.assertFalse(allObjectives.isEmpty());
    
  }
}
