package eu.scape_project.watch.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.dao.DAO;
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

/**
 * 
 * Check if KB deals well with concurrency.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class KBConcurrencyTest {
  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(KBConcurrencyTest.class);

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
   * Testing concurrent writes.
   */
  @Test
  public void testConcurrentWrites() {

    final long startTimestamp = System.nanoTime();
    LOG.info("Starting concurrent execution");
    final ExecutorService pool = Executors.newFixedThreadPool(10);

    final int numberOfTypes = 100;
    final int numberOfEntitiesPerType = 100;
    final int numberOfPropertiesPerType = 10;

    final Source source = new Source("Source 1", "");
    final SourceAdaptor adaptor = new SourceAdaptor("Plugin 1", "0.0.1", "default", source,
      new ArrayList<EntityType>(), new ArrayList<Property>(), new HashMap<String, String>());

    DAO.save(source);
    DAO.save(adaptor);

    for (int i = 0; i < numberOfTypes; i++) {
      final int runnableIndex = i;
      pool.submit(new Runnable() {

        @Override
        public void run() {
          LOG.debug("Task {}", runnableIndex);
          final EntityType type = new EntityType("Type " + runnableIndex, "");
          DAO.save(type);

          final List<Property> properties = new ArrayList<Property>();

          for (int k = 0; k < numberOfPropertiesPerType; k++) {
            final Property property = new Property(type, "Property " + k, "");
            properties.add(property);
            DAO.save(property);
          }

          final List<Entity> entities = new ArrayList<Entity>();

          for (int j = 0; j < numberOfEntitiesPerType; j++) {
            final Entity entity = new Entity(type, "Entity " + j);
            entities.add(entity);
            DAO.save(entity);
          }

          for (int k = 0; k < numberOfPropertiesPerType; k++) {
            for (int j = 0; j < numberOfEntitiesPerType; j++) {
              PropertyValue pv;
              try {
                pv = new PropertyValue(entities.get(j), properties.get(k), "Value " + k + "x" + j);
                DAO.PROPERTY_VALUE.save(adaptor, pv);
              } catch (final UnsupportedDataTypeException e) {
                LOG.error("Error saving property value", e);
              } catch (final InvalidJavaClassForDataTypeException e) {
                LOG.error("Error saving property value", e);
              }
            }
          }

        }
      });
    }

    // SHUTDOWN AND AWAIT TERMINATION
    pool.shutdown();
    try {
      if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
        pool.shutdownNow();
        if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
          LOG.error("Pool did not terminate");
        }
      }
    } catch (final InterruptedException ie) {
      pool.shutdownNow();
      Thread.currentThread().interrupt();
    }

    // STATISTICS
    final long endTimestamp = System.nanoTime();
    final long duration = endTimestamp - startTimestamp;
    final int count = numberOfTypes + numberOfTypes * numberOfEntitiesPerType + numberOfTypes
      * numberOfPropertiesPerType + numberOfTypes * numberOfPropertiesPerType * numberOfEntitiesPerType + 2;
    final double throughput = ((double) count * 1000000) / duration;
    LOG.info("Duration {}s", duration / 1000000);
    LOG.info("Number of records {}", count);
    LOG.info("Throughput was {} records/s", throughput);

    // VALIDATION
    final List<EntityType> listOfTypes = DAO.ENTITY_TYPE.query("", 0, numberOfTypes);
    // LOG.info("Types: {}", listOfTypes);
    Assert.assertEquals(numberOfTypes, listOfTypes.size());

    final int typeCount = DAO.ENTITY_TYPE.count("");
    Assert.assertEquals(numberOfTypes, typeCount);

    final int propertyCount = DAO.PROPERTY.count("");
    Assert.assertEquals(numberOfTypes * numberOfPropertiesPerType, propertyCount);

    final int entityCount = DAO.ENTITY.count("");
    Assert.assertEquals(numberOfTypes * numberOfEntitiesPerType, entityCount);

  }

}
