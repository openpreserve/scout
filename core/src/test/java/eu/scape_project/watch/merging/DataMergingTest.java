package eu.scape_project.watch.merging;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * Test the merging of data into the knowledge base.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class DataMergingTest {

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DataMergingTest.class.getSimpleName());

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
    LOG.info("Deleting data folder at " + DATA_TEMP_DIR);
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(new File(DATA_TEMP_DIR));
  }

  /**
   * Test if merging the same entity twice would not result on a duplicated
   * entity.
   */
  @Test
  public void mergeDefaultSameEntityTest() {
    LOG.debug("Start merge default same entity test");
    final DataMerger merger = new DataMerger();

    final EntityType type = new EntityType("format", "file format");

    final Entity entity1 = new Entity(type, "image/png");
    final Entity entity2 = new Entity(type, "image/png");

    merger.merge(entity1);
    merger.merge(entity2);

    final int count = DAO.ENTITY.count("");
    Assert.assertEquals(1, count);

    DAO.delete(entity1);
  }

  /**
   * Test if merging the same property value twice would not result on a
   * duplicated property value.
   * 
   * @throws InvalidJavaClassForDataTypeException
   *           Unexpected exception
   * @throws UnsupportedDataTypeException
   *           Unexpected exception
   */
  @Test
  public void mergeDefaultSamePropertyValueTest() throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    LOG.debug("Start merge default same property value test");
    final DataMerger merger = new DataMerger();

    final EntityType type = new EntityType("format", "file format");
    final Property property = new Property(type, "PUID", "PRONOM Unique Identifier", DataType.STRING);

    final Entity entity = new Entity(type, "image/png");
    final PropertyValue value1 = new PropertyValue(entity, property, "fmt/123");
    final PropertyValue value2 = new PropertyValue(entity, property, "fmt/123");

    final Source source = new Source("PRONOM", "PRONOM Registry");
    final SourceAdaptor adaptor = new SourceAdaptor("PRONOM SPARQL Endpoint Adaptor", "0.0.1", "default", source,
      Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    merger.merge(entity);
    merger.merge(adaptor, value1);
    merger.merge(adaptor, value2);

    final int count = DAO.PROPERTY_VALUE.count("");
    Assert.assertEquals(1, count);

    DAO.delete(type);
    DAO.delete(property);

    DAO.delete(entity);
    DAO.delete(value1);

    DAO.delete(source);
    DAO.delete(adaptor);
  }

  /**
   * Test if merging two different entities would result in two entities on the
   * knowledge base.
   */
  @Test
  public void mergeDefaultDifferentEntitiesTest() {
    LOG.debug("Start merge default different entities test");
    final DataMerger merger = new DataMerger();

    final EntityType type = new EntityType("format", "file format");

    final Entity entity1 = new Entity(type, "image/png");
    final Entity entity2 = new Entity(type, "image/tiff");

    merger.merge(entity1);
    merger.merge(entity2);

    final int count = DAO.ENTITY.count("");
    Assert.assertEquals(2, count);

    DAO.delete(type);
    DAO.delete(entity1, entity2);
  }

  /**
   * Test merging different property values.
   * 
   * @throws InvalidJavaClassForDataTypeException
   *           Unexpected exception
   * @throws UnsupportedDataTypeException
   *           Unexpected exception
   */
  @Test
  public void mergeDefaultDifferentPropertyValueTest() throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {
    LOG.debug("Start merge default different property value test");
    final DataMerger merger = new DataMerger();

    final EntityType type = new EntityType("format", "file format");
    final Property property = new Property(type, "PUID", "PRONOM Unique Identifier", DataType.STRING);

    final Entity entity = new Entity(type, "image/png");
    final PropertyValue value1 = new PropertyValue(entity, property, "fmt/123");
    final PropertyValue value2 = new PropertyValue(entity, property, "fmt/321");

    final Source source = new Source("PRONOM", "PRONOM Registry");
    final SourceAdaptor adaptor = new SourceAdaptor("PRONOM SPARQL Endpoint Adaptor", "0.0.1", "default", source,
      Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    merger.merge(entity);
    merger.merge(adaptor, value1);
    merger.merge(adaptor, value2);

    final int count = DAO.PROPERTY_VALUE.count("");
    Assert.assertEquals(2, count);

    DAO.delete(type);
    DAO.delete(property);

    DAO.delete(entity);
    DAO.delete(value1, value2);

    DAO.delete(source);
    DAO.delete(adaptor);
  }

  /**
   * Test if special rules are called for entities.
   */
  @Test
  public void specialEntityRulesTest() {
    LOG.debug("Start special entity rules test");
    final DataMerger merger = new DataMerger();
    final MergeRule rule = Mockito.mock(MergeRule.class);

    final EntityType type = new EntityType("format", "file format");
    final Entity entity1 = new Entity(type, "image/png");

    merger.setMergeRule(type, rule);

    merger.merge(entity1);

    Mockito.verify(rule).mergeEntity(entity1);
  }

  /**
   * Test if special rules are called for properties.
   * 
   * @throws InvalidJavaClassForDataTypeException
   *           Unexpected exception
   * @throws UnsupportedDataTypeException
   *           Unexpected exception
   */
  @Test
  public void specialPropertyRulesTest() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {
    LOG.debug("Start special property rules test");
    final DataMerger merger = new DataMerger();
    final MergeRule rule = Mockito.mock(MergeRule.class);

    final EntityType type = new EntityType("format", "file format");
    final Property property = new Property(type, "PUID", "PRONOM Unique Identifier", DataType.STRING);
    final Entity entity = new Entity(type, "image/png");
    final PropertyValue value = new PropertyValue(entity, property, "fmt/123");

    final Source source = new Source("PRONOM", "PRONOM Registry");
    final SourceAdaptor adaptor = new SourceAdaptor("PRONOM SPARQL Endpoint Adaptor", "0.0.1", "default", source,
      Arrays.asList(type), Arrays.asList(property), new HashMap<String, String>());

    merger.setMergeRule(property, rule);

    merger.merge(adaptor, value);

    Mockito.verify(rule).mergePropertyValue(adaptor, value);
  }

}
