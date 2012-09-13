package eu.scape_project.watch.model;

import java.io.File;
import java.io.IOException;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

/**
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * @author Petar Petrov
 */
public class SourceAdaptorListenerTest {

  /**
   * Temporary data folder.
   */
  private File dataTempDir;

  /**
   * Setup knowledge base.
   * 
   * @throws IOException
   *           Error connecting to data folder.
   */
  @Before
  public void setup() throws IOException {
    dataTempDir = JavaUtils.createTempDirectory();
    KBUtils.dbConnect(dataTempDir.getPath(), false);
  }

  /**
   * Delete temporary data folder.
   */
  @After
  public void tearDown() {
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempDir);
  }

  /**
   * Test Source adaptor listeners.
   */
  @Test
  public void testSourceAdaptorListeners() {

    @SuppressWarnings("unchecked")
    final DOListener<SourceAdaptor> mockDOListener = Mockito.mock(DOListener.class);

    DAO.addDOListener(SourceAdaptor.class, mockDOListener);

    final SourceAdaptor test = new SourceAdaptor("test", "0.1", "demo", null, null, null, null);

    DAO.save(test);
    Mockito.verify(mockDOListener).onUpdated(test);

    DAO.delete(test);
    Mockito.verify(mockDOListener).onRemoved(test);

    DAO.removeDOListener(SourceAdaptor.class, mockDOListener);

  }
}
