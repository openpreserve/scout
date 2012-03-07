package eu.scape_project.watch.components.adaptors.c3po;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import eu.scape_project.watch.components.Adaptor;
import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A watch conforming adaptor for a collection profile source called c3po.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * @version 0.1
 */
public class C3POAdaptor extends Adaptor {

  /**
   * Default logger for this adaptor.
   */
  private static final Logger LOG = LoggerFactory.getLogger(C3POAdaptor.class);

  /**
   * The api endpoint of the c3po source.
   */
  private static String cpEndpoint = "";

  /**
   * The properties supported by this version of the adaptor.
   */
  private List<String> supportedProperties;

  /**
   * The configuration of this adaptor instance.
   */
  private Properties properties;

  /**
   * The source client.
   */
  private IC3POClient source;

  /**
   * The default constructor inits the supported properties and reads in the
   * default configuration.
   */
  public C3POAdaptor() {
    this.init();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkForTask(final Task t) {
    final EntityType type = t.getEntity().getEntityType();

    if (type == null) {
      LOG.warn("EntityType of the provided entity is null. Cannot determine if supported.");
      return false;
    }

    // TODO this should be a constant or similar.
    if (!type.getName().equals("COLLECTION_PROFILE")) {
      LOG.warn("EntityType {} is not supported", type.getName());
      return false;
    }

    for (String p : this.supportedProperties) {
      if (t.getProperty().getName().equals(p)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Retrieves the configuration value for the passed key of the loaded
   * properties. If the key is missing or the properties were not loaded
   * successfully the empty string is returned.
   * 
   * @param key
   *          the key of the config.
   * @return retrieves the value of the configuration key or the empty string if
   *         not present.
   */
  public String getConfig(final String key) {
    String config = "";
    if (this.properties != null) {
      config = this.properties.getProperty(key, "");
    }

    return config;
  }

  /**
   * Fetches dummy data from a profile output. However the no real calls to the
   * REST API of c3po are done yet.
   */
  @Override
  protected void fetchData() {

    List<String> identifiers = this.source.getCollectionIdentifiers();

    for (Task t : this.getTasks()) {

      String id = t.getEntity().getName();
      if (identifiers.contains(id)) {

        // this should be t.getProperties();
        List<String> properties = this.generatePropertyExpansionList(t.getProperty());
        String uuid = this.source.submitCollectionProfileJob(id, properties);

        InputStream is = this.source.pollJobResult(uuid);
        while (is == null) {
          is = this.source.pollJobResult(uuid);
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            LOG.warn("An error occurred while waiting for c3po result: {}", e.getMessage());
          }
        }

        C3POProfileReader reader = new C3POProfileReader(is);
        String name = reader.getCollectionName();

        Entity e = new Entity(t.getEntity().getEntityType(), name);
        Property p = t.getProperty();
        PropertyValue pv = null;

        if (p.getName().equals(C3POConstants.CP_COLLECTION_IDENTIFIER)) {
          pv = new PropertyValue(e, p, name);
        } else if (p.getName().equals(C3POConstants.CP_OBJECTS_COUNT)) {
          String count = reader.getObjectsCount();
          pv = new PropertyValue(e, p, count);
        }

        Result r = new Result(e, p, pv);
        this.results.add(r);
      }
    }

  }

  private List<String> generatePropertyExpansionList(Property property) {
    LOG.debug("generating parameter for property {}", property.getName());
    return Arrays.asList(property.getName());
  }

  private void init() {
    // may be this should be determined by the config
    this.supportedProperties = new ArrayList<String>();
    this.supportedProperties.add(C3POConstants.CP_COLLECTION_IDENTIFIER);
    this.supportedProperties.add(C3POConstants.CP_COLLECTION_SIZE);
    this.supportedProperties.add(C3POConstants.CP_OBJECTS_COUNT);
    this.supportedProperties.add(C3POConstants.CP_OBJECTS_AVG_SIZE);
    this.supportedProperties.add(C3POConstants.CP_OBJECTS_MAX_SIZE);
    this.supportedProperties.add(C3POConstants.CP_OBJECTS_MIN_SIZE);
    this.supportedProperties.add(C3POConstants.CP_FORMAT_DISTRIBUTION);
    this.supportedProperties.add(C3POConstants.CP_FORMAT_MODE);
    this.supportedProperties.add(C3POConstants.CP_MIMETYPE_DISTRIBUTION);
    this.supportedProperties.add(C3POConstants.CP_MIMETYPE_MODE);
    this.supportedProperties.add(C3POConstants.CP_PUID_DISTRIBUTION);
    this.supportedProperties.add(C3POConstants.CP_PUID_MODE);

    try {
      final InputStream stream = this.getClass().getClassLoader()
        .getResourceAsStream("adaptors/c3po/config.properties");
      this.properties = new Properties();
      this.properties.load(stream);
      stream.close();
    } catch (final Exception e) {
      LOG.error("An error occurred while reading the config file: {}", e.getMessage());
    }

    this.initSourceClient();
  }

  private void initSourceClient() {
    cpEndpoint = this.getConfig("c3po.endpoint");
    if (cpEndpoint.equals("") || cpEndpoint.equals("dummy")) {
      LOG.info("initializing a dummy source client for the c3po adaptor");
      this.source = new C3PODummyClient();
    } else {
      LOG.info("initializing a production source client for the c3po adaptor with api bound at: {}", cpEndpoint);
      this.source = new C3POClient(cpEndpoint);
    }
  }
}
