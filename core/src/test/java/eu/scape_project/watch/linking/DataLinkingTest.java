package eu.scape_project.watch.linking;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.merging.DataMerger;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * Test the linking of data in the knowledge base.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class DataLinkingTest {

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DataLinkingTest.class);

  /**
   * A temporary directory to hold the data.
   */
  private File dataTempDir;

  /**
   * Initialize the data folder.
   * 
   * @throws IOException
   */
  @Before
  public void before() throws IOException {
    dataTempDir = JavaUtils.createTempDirectory();
    KBUtils.dbConnect(dataTempDir.getPath(), false);
  }

  /**
   * Cleanup the data folder.
   */
  @After
  public void afterClass() {
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempDir);
  }

  /**
   * Test if rules are correctly called when entities are created.
   */
  @Test
  public void entityCreatedRuleCallingTest() {
    LOG.debug("Start entity created rule calling test");
    final DataLinker linker = new DataLinker();
    final DataMerger merger = new DataMerger();

    final EntityType type1 = new EntityType("format", "file format");
    final EntityType type2 = new EntityType("tool", "migrator");

    final Entity entity1 = new Entity(type1, "image/png");
    final Entity entity2 = new Entity(type2, "imagemagick");

    final LinkRule rule1 = Mockito.mock(LinkRule.class);
    Mockito.when(rule1.getEntityTypesObserved()).thenReturn(Arrays.asList(type1));

    final LinkRule rule2 = Mockito.mock(LinkRule.class);
    Mockito.when(rule2.getEntityTypesObserved()).thenReturn(Arrays.asList(type2));

    linker.addLinkRule(rule1);
    linker.addLinkRule(rule2);
    linker.removeLinkRule(rule2);

    DAO.save(type1);

    merger.merge(entity1);
    merger.merge(entity2);

    Mockito.verify(rule1).findAndCreateLinks(entity1);
    Mockito.verify(rule1, Mockito.never()).findAndCreateLinks(entity2);
    Mockito.verify(rule2, Mockito.never()).findAndCreateLinks(entity1);
    Mockito.verify(rule2, Mockito.never()).findAndCreateLinks(entity2);

    DAO.delete(type1, type2);
    DAO.delete(entity1, entity2);
  }

  /**
   * Test if rules are correctly called when property values are created.
   * 
   * @throws InvalidJavaClassForDataTypeException
   *           Unexpected exception
   * @throws UnsupportedDataTypeException
   *           Unexpected exception
   */
  @Test
  public void propertyValueCreatedRuleCallingTest() throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    LOG.debug("Start property value created rule calling test");
    final DataLinker linker = new DataLinker();
    final DataMerger merger = new DataMerger();

    final EntityType type = new EntityType("format", "file format");
    final Entity entity = new Entity(type, "image/png;version=1.2");

    final Property property1 = new Property(type, "mime", "mime type", DataType.STRING);
    final Property property2 = new Property(type, "puid", "pronom id", DataType.STRING);

    final Source source = new Source("registry", "internal registry");
    final SourceAdaptor adaptor = new SourceAdaptor("x", "0.0.1", "default", source, Arrays.asList(type),
      Arrays.asList(property1, property2), new HashMap<String, String>());

    final PropertyValue value1 = new PropertyValue(entity, property1, "image/png");
    final PropertyValue value2 = new PropertyValue(entity, property2, "fmt/13");

    final LinkRule rule1 = Mockito.mock(LinkRule.class);
    Mockito.when(rule1.getPropertiesObserved()).thenReturn(Arrays.asList(property1));

    final LinkRule rule2 = Mockito.mock(LinkRule.class);
    Mockito.when(rule2.getPropertiesObserved()).thenReturn(Arrays.asList(property2));

    linker.addLinkRule(rule1);
    linker.addLinkRule(rule2);
    linker.removeLinkRule(rule2);

    DAO.save(type);
    DAO.save(property1, property2);
    DAO.save(source);
    DAO.save(adaptor);

    merger.merge(entity);
    merger.merge(adaptor, value1);
    merger.merge(adaptor, value2);

    Mockito.verify(rule1).findAndCreateLinks(value1);
    Mockito.verify(rule1, Mockito.never()).findAndCreateLinks(value2);
    Mockito.verify(rule2, Mockito.never()).findAndCreateLinks(value1);
    Mockito.verify(rule2, Mockito.never()).findAndCreateLinks(value2);

    LOG.debug("clearing data");
    DAO.delete(type);
    DAO.delete(entity);
    DAO.delete(property1, property2);
    DAO.delete(source);
    DAO.delete(adaptor);
    DAO.delete(value1, value2);
  }
}
