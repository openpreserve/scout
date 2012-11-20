package eu.scape_project.watch.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

public class DictionaryAndListSavingSupportTest {

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DictionaryAndListSavingSupportTest.class);

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
    LOG.info("Deleting data folder at " + dataTempir);
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempir);
  }

  /**
   * Testing string dictionary save.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testStringDictionaryValueSaving() throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {

    final EntityType type = new EntityType("type", "test");
    final Entity entity = new Entity(type, "entity");
    final Property property = new Property(type, "property", "", DataType.STRING_DICTIONARY);
    final Source source = new Source("source", "");
    final SourceAdaptor adaptor = new SourceAdaptor("adaptor", "0.0.1", "default", source, new ArrayList<EntityType>(),
      new ArrayList<Property>(), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);

    List<DictionaryItem> value1 = new ArrayList<DictionaryItem>();
    value1.add(new DictionaryItem("image/jpeg", "1000"));
    value1.add(new DictionaryItem("image/png", "100"));

    List<DictionaryItem> value2 = new ArrayList<DictionaryItem>();
    value2.add(new DictionaryItem("image/jpeg", "10000"));
    value2.add(new DictionaryItem("image/png", "1000"));
    value2.add(new DictionaryItem("image/gif", "10"));

    final PropertyValue pv1 = DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, value1));
    final PropertyValue pv2 = DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, value1));
    final PropertyValue pv3 = DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, value2));

    final int versionCount = DAO.PROPERTY_VALUE.countWithEntityAndProperty(entity.getId(), property.getId());
    Assert.assertEquals(2, versionCount);

    final int pv1MeasurementCount = DAO.MEASUREMENT.countByPropertyValue(pv1, false);
    Assert.assertEquals(2, pv1MeasurementCount);

    final int pv1SignificantMeasurementCount = DAO.MEASUREMENT.countByPropertyValue(pv1, true);
    Assert.assertEquals(2, pv1SignificantMeasurementCount);

    int countAllMeasurements = DAO.MEASUREMENT.countByEntityAndProperty(entity.getId(), property.getId(), false);
    Assert.assertEquals(3, countAllMeasurements);

    int countSignificantMeasurements = DAO.MEASUREMENT.countByEntityAndProperty(entity.getId(), property.getId(), true);
    Assert.assertEquals(3, countSignificantMeasurements);
  }

  /**
   * Testing string list save.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testStringListValueSaving() throws UnsupportedDataTypeException, InvalidJavaClassForDataTypeException {

    final EntityType type = new EntityType("type", "test");
    final Entity entity = new Entity(type, "entity");
    final Property property = new Property(type, "property", "", DataType.STRING_LIST);
    final Source source = new Source("source", "");
    final SourceAdaptor adaptor = new SourceAdaptor("adaptor", "0.0.1", "default", source, new ArrayList<EntityType>(),
      new ArrayList<Property>(), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);

    List<String> value1 = new ArrayList<String>();
    value1.add("image/jpeg");
    value1.add("image/png");

    List<String> value2 = new ArrayList<String>();
    value2.add("image/jpeg");
    value2.add("image/png");
    value2.add("image/gif");

    final PropertyValue pv1 = DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, value1));
    final PropertyValue pv2 = DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, value1));
    final PropertyValue pv3 = DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, value2));

    final int versionCount = DAO.PROPERTY_VALUE.countWithEntityAndProperty(entity.getId(), property.getId());
    Assert.assertEquals(2, versionCount);

    final int pv1MeasurementCount = DAO.MEASUREMENT.countByPropertyValue(pv1, false);
    Assert.assertEquals(2, pv1MeasurementCount);

    final int pv1SignificantMeasurementCount = DAO.MEASUREMENT.countByPropertyValue(pv1, true);
    Assert.assertEquals(2, pv1SignificantMeasurementCount);

    int countAllMeasurements = DAO.MEASUREMENT.countByEntityAndProperty(entity.getId(), property.getId(), false);
    Assert.assertEquals(3, countAllMeasurements);

    int countSignificantMeasurements = DAO.MEASUREMENT.countByEntityAndProperty(entity.getId(), property.getId(), true);
    Assert.assertEquals(3, countSignificantMeasurements);
  }

}
