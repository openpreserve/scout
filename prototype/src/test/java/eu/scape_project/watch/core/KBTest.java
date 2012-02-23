package eu.scape_project.watch.core;

import com.google.common.io.Files;
import eu.scape_project.watch.core.dao.EntityTypeDAO;
import eu.scape_project.watch.core.model.EntityType;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import thewebsemantic.Sparql;

/**
 * 
 * Unit tests of the Knowledge Base persistence and access.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class KBTest {
  /**
   * The logger.
   */
  private static final Logger LOG = Logger.getLogger(KBTest.class.getSimpleName());

  /**
   * A temporary directory to hold the data.
   */
  private static final File DATA_TEMP_DIR = Files.createTempDir();

  /**
   * Initialize the data folder.
   */
  @BeforeClass
  public static void beforeClass() {
    LOG.info("Creating data folder at " + DATA_TEMP_DIR.getPath());
    KB.setDataFolder(DATA_TEMP_DIR.getPath());
  }

  /**
   * Cleanup the data folder.
   */
  @AfterClass
  public static void afterClass() {
    LOG.info("Deleting data folder at " + DATA_TEMP_DIR.getPath());
    FileUtils.deleteQuietly(DATA_TEMP_DIR);
  }

  /**
   * Test Entity Type CRUD operations.
   */
  @Test
  public void testEntityType() {
    KB.getInstance();

    // CREATE
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    type.save();

    KBUtils.printStatements();

    // List
    final Collection<EntityType> types = KB.getInstance().getReader().load(EntityType.class);

    Assert.assertTrue(types.contains(type));

    // QUERY
    final String query = KB.PREFIXES_DECL + "SELECT ?s WHERE { ?s rdf:type watch:EntityType }";
    final List<EntityType> types2 = Sparql.exec(KB.getInstance().getModel(), EntityType.class, query);

    Assert.assertTrue(types2.contains(type));

    // FIND
    final EntityType type2 = EntityTypeDAO.findById(type.getName());

    Assert.assertNotNull(type2);
    Assert.assertEquals(type, type2);

    // DELETE
    type.delete();

    // LIST AGAIN
    final Collection<EntityType> types3 = KB.getInstance().getReader().load(EntityType.class);

    Assert.assertFalse(types3.contains(type));

    // QUERY AGAIN
    final List<EntityType> types4 = Sparql.exec(KB.getInstance().getModel(), EntityType.class, query);

    Assert.assertFalse(types4.contains(type));

    // FIND AGAIN
    final EntityType type3 = EntityTypeDAO.findById(type.getName());
    Assert.assertNull(type3);

  }

}
