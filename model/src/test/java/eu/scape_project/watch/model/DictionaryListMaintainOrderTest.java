package eu.scape_project.watch.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

public class DictionaryListMaintainOrderTest {

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DictionaryListMaintainOrderTest.class);

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
  public void testStringDictionaryValueOrder() throws UnsupportedDataTypeException,
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
    value1.add(new DictionaryItem("a1", "1000"));
    value1.add(new DictionaryItem("a2", "100"));
    value1.add(new DictionaryItem("a3", "100"));
    value1.add(new DictionaryItem("a4", "100"));
    value1.add(new DictionaryItem("a5", "100"));
    value1.add(new DictionaryItem("a6", "100"));
    value1.add(new DictionaryItem("a7", "100"));
    value1.add(new DictionaryItem("a8", "100"));
    value1.add(new DictionaryItem("a9", "100"));

    final PropertyValue pv1 = DAO.PROPERTY_VALUE.save(adaptor, new PropertyValue(entity, property, value1));

    final PropertyValue pv2 = DAO.PROPERTY_VALUE.find(entity.getId(), property.getId());

    final List<DictionaryItem> value2 = (List<DictionaryItem>) pv2.getValue(List.class);
    Assert.assertEquals(value1, value2);

  }
}
