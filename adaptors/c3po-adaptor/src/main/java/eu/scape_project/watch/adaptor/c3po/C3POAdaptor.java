package eu.scape_project.watch.adaptor.c3po;

import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_COLLECTION_SIZE;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_DESCRIPTION;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_FORMAT_DISTRIBUTION;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_NAME;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_OBJECTS_AVG_SIZE;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_OBJECTS_COUNT;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_OBJECTS_MAX_SIZE;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_OBJECTS_MIN_SIZE;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.ENDPOINT_CNF;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.ENDPOINT_DEFAULT;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.ENDPOINT_DESC;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import eu.scape_project.watch.adaptor.c3po.client.C3POClient;
import eu.scape_project.watch.adaptor.c3po.client.C3POClientInterface;
import eu.scape_project.watch.adaptor.c3po.client.C3PODummyClient;
import eu.scape_project.watch.adaptor.c3po.command.CollectionSizeCommand;
import eu.scape_project.watch.adaptor.c3po.command.Command;
import eu.scape_project.watch.adaptor.c3po.command.DistributionCommand;
import eu.scape_project.watch.adaptor.c3po.command.ObjectsAvgSizeCommand;
import eu.scape_project.watch.adaptor.c3po.command.ObjectsCountCommand;
import eu.scape_project.watch.adaptor.c3po.command.ObjectsMaxSizeCommand;
import eu.scape_project.watch.adaptor.c3po.command.ObjectsMinSizeCommand;
import eu.scape_project.watch.adaptor.c3po.common.ProfileResult;
import eu.scape_project.watch.common.ConfigParameter;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.plugin.PluginType;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

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
   * The current version of the adaptor.
   */
  private static final String VERSION = "0.0.1";

  /**
   * The current config of c3po.
   */
  private Map<String, String> config;

  /**
   * The commands for property extraction.
   */
  private Map<String, Command> commands;

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
  private C3POClientInterface source;

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
    String cnf = "";
    if (this.properties != null) {
      cnf = this.properties.getProperty(key, "");
    }

    return cnf;
  }

  @Override
  public void init() throws PluginException {
    this.initConfigs();

    final EntityType cp = new EntityType(CP_NAME, CP_DESCRIPTION);
    final Property size = new Property(cp, CP_COLLECTION_SIZE, "Collection size");
    final Property count = new Property(cp, CP_OBJECTS_COUNT, "Objects count");
    final Property avgSize = new Property(cp, CP_OBJECTS_AVG_SIZE, "Objects avg size");
    final Property minSize = new Property(cp, CP_OBJECTS_MIN_SIZE, "Objects min size");
    final Property maxSize = new Property(cp, CP_OBJECTS_MAX_SIZE, "Objects max size");
    final Property formatDistr = new Property(cp, CP_FORMAT_DISTRIBUTION, "Collection format distribution");

    this.commands = new HashMap<String, Command>();
    this.commands.put(CP_COLLECTION_SIZE, new CollectionSizeCommand(size));
    this.commands.put(CP_OBJECTS_COUNT, new ObjectsCountCommand(count));
    this.commands.put(CP_OBJECTS_MAX_SIZE, new ObjectsMaxSizeCommand(maxSize));
    this.commands.put(CP_OBJECTS_MIN_SIZE, new ObjectsMinSizeCommand(minSize));
    this.commands.put(CP_OBJECTS_AVG_SIZE, new ObjectsAvgSizeCommand(avgSize));
    this.commands.put(CP_FORMAT_DISTRIBUTION, new DistributionCommand(formatDistr, "format"));
  }

  @Override
  public void shutdown() throws PluginException {
    this.defaultConfig.clear();
    this.config.clear();
    this.commands.clear();

    // close connections, cancel jobs, etc.
  }

  @Override
  public String getName() {
    return "c3po";
  }

  @Override
  public String getVersion() {
    return VERSION;
  }

  @Override
  public String getDescription() {
    return "A scout adaptor for the c3po content profiler source";
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
  public ResultInterface execute() throws PluginException {
    LOG.info("Executing c3po adaptor, fetching all properties for all known collections.");
    this.createSource();

    final List<String> identifiers = this.source.getCollectionIdentifiers();

    LOG.debug("c3po adaptor has found {} collections, ... fetching", identifiers.size());

    final ProfileResult result = new ProfileResult();
    final EntityType et = new EntityType(CP_NAME, CP_DESCRIPTION);

    for (String id : identifiers) {
      final Entity e = new Entity(et, id);
      final List<PropertyValue> values = this.getPropertyValues(id, null);

      for (PropertyValue v : values) {
        v.setEntity(e);
      }

      result.add(values);
    }

    return result;
  }

  @Override
  public ResultInterface execute(final Map<Entity, List<Property>> context) throws PluginException {
    throw new PluginException("This method is not yet implemented!");
  }

  private void initConfigs() {
    this.config = new HashMap<String, String>();

    this.defaultConfig = new ArrayList<ConfigParameter>();
    this.defaultConfig.add(new ConfigParameter(ENDPOINT_CNF, ENDPOINT_DEFAULT, ENDPOINT_DESC, true));

    for (final ConfigParameter cp : this.defaultConfig) {
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
    final String endpoint = this.getParameterValues().get(ENDPOINT_CNF);
    if (endpoint == null || endpoint.equals("")) {
      throw new PluginException("The endpoint value is invalued: " + endpoint);
    }

    if (endpoint.equals(ENDPOINT_DEFAULT)) {
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
    final List<PropertyValue> values = new ArrayList<PropertyValue>();

    if (stream != null) {
      final C3POProfileReader reader = new C3POProfileReader(stream);
      this.setupCommands(reader);
      if (props == null || props.size() == 0) {

        for (String c : this.commands.keySet()) {
          final Command cmd = this.commands.get(c);
          final PropertyValue pv = cmd.execute();
          values.add(pv);
        }
      } else {
        // TODO start only those matching the property names.
        throw new RuntimeException("Not yet implemented");
      }
    }

    LOG.info("Returning property values...");
    return values;
  }

  private void setupCommands(final C3POProfileReader reader) {
    for (Command cmd : this.commands.values()) {
      cmd.setReader(reader);
    }
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
