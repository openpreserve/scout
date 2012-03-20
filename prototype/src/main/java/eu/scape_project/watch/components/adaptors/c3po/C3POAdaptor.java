package eu.scape_project.watch.components.adaptors.c3po;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import eu.scape_project.watch.components.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.core.model.DictionaryItem;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyDataStructure;
import eu.scape_project.watch.core.model.PropertyValue;
import eu.scape_project.watch.core.plugin.ConfigParameter;
import eu.scape_project.watch.core.plugin.InvalidParameterException;
import eu.scape_project.watch.core.plugin.PluginException;
import eu.scape_project.watch.core.plugin.PluginType;
import eu.scape_project.watch.core.plugin.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A watch conforming adaptor for a collection profile source called c3po.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * @version 0.1
 */
public class C3POAdaptor implements AdaptorPluginInterface {

  /**
   * Default logger for this adaptor.
   */
  private static final Logger LOG = LoggerFactory.getLogger(C3POAdaptor.class);

  /**
   * The api endpoint of the c3po source.
   */
  private static String cpEndpoint = "";

  /**
   * The current config of c3po.
   */
  private Map<String, String> config;

  /**
   * The default configs.
   */
  private List<ConfigParameter> defaultConfig;

  /**
   * The configuration of this adaptor instance.
   */
  private Properties properties;

  /**
   * The source client.
   */
  private IC3POClient source;

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

  @Override
  public void init() throws PluginException {
    this.initConfigs();
  }

  @Override
  public void shutdown() throws PluginException {
    this.defaultConfig.clear();
    this.config.clear();

    // close connections, cancel jobs, etc.
  }

  @Override
  public String getName() {
    return "c3po";
  }

  @Override
  public String getVersion() {
    return "0.1";
  }

  @Override
  public String getDescription() {
    return "A watch adaptor for the c3po content profiler source";
  }

  @Override
  public PluginType getPluginType() {
    return PluginType.ADAPTOR;
  }

  @Override
  public List<ConfigParameter> getParameters() {
    return this.defaultConfig;
  }

  @Override
  public Map<String, String> getParameterValues() {
    return this.config;
  }

  @Override
  public void setParameterValues(final Map<String, String> values) throws InvalidParameterException {
    final Set<String> keys = values.keySet();

    for (ConfigParameter cp : this.defaultConfig) {
      final String key = cp.getKey();
      if (cp.isRequired() && (!keys.contains(key) || values.get(key) == null)) {
        throw new InvalidParameterException("No value set for the required config parameter: " + key);
      }
    }

    this.config = values;

  }

  @Override
  public Result execute() throws PluginException {
    LOG.info("Hello from c3po");
    this.createSource();
    final List<String> identifiers = this.source.getCollectionIdentifiers();
    for (String id : identifiers) {
      this.getPropertyValues(id, null);
      // TODO capture result and generate real result object.
    }

    return null;
  }

  @Override
  public Result execute(final Map<Entity, List<Property>> context) throws PluginException {
    LOG.info("Hello from c3po, reading config...");
    this.createSource();

    final List<String> identifiers = this.source.getCollectionIdentifiers();
    for (Entity e : context.keySet()) {
      if (identifiers.contains(e.getName())) {
        this.getPropertyValues(e.getName(), context.get(e));
        // TODO capture result and generate real result object.
      }
    }

    return null;
  }

  private void initConfigs() {
    this.config = new HashMap<String, String>();

    this.defaultConfig = new ArrayList<ConfigParameter>();
    this.defaultConfig.add(new ConfigParameter(C3POConstants.ENDPOINT_CNF, C3POConstants.ENDPOINT_DEFAULT,
      C3POConstants.ENDPOINT_DESC, true));

    for (ConfigParameter cp : this.defaultConfig) {
      this.config.put(cp.getKey(), cp.getValue());
    }
  }

  private InputStream getCollectionProfile(String id) {
    final List<String> expanded = this.generatePropertyExpansionList(null);
    final String uuid = this.source.submitCollectionProfileJob(id, expanded);
    int counter = 1;

    InputStream is = this.source.pollJobResult(uuid);
    while (is == null && counter < 10) {
      is = this.source.pollJobResult(uuid);
      counter++;
      try {
        Thread.sleep(5000);
      } catch (final InterruptedException e) {
        LOG.warn("An error occurred while waiting for c3po's result: {}", e.getMessage());
      }
    }

    return is;
  }

  private void createSource() throws PluginException {
    final String endpoint = this.getParameterValues().get(C3POConstants.ENDPOINT_CNF);
    if (endpoint == null || endpoint.equals("")) {
      throw new PluginException("The endpoint value is invalued: " + endpoint);
    }

    if (endpoint.equals(C3POConstants.ENDPOINT_DEFAULT)) {
      this.source = new C3PODummyClient();
    } else {
      this.source = new C3POClient(endpoint);
    }

    if (this.source == null) {
      throw new PluginException("An error occurred, could not initialize a c3po client");
    }
  }

  private List<PropertyValue> getPropertyValues(final String id, final List<Property> props) {
    final InputStream stream = this.getCollectionProfile(id);

    if (stream != null) {
      final C3POProfileReader reader = new C3POProfileReader(stream);
      if (props == null || props.size() == 0) {
        // TODO start all commands
      } else {
        // TODO start only those matching the property names.
      }
    }

    LOG.info("Returning property values...");
    return new ArrayList<PropertyValue>();
  }

  private List<String> generatePropertyExpansionList(Property property) {
    List<String> props = new ArrayList<String>();
    if (property != null) {
      LOG.debug("generating parameter for property {}", property.getName());
      props = Arrays.asList(property.getName());
    }
    return props;
  }

}
