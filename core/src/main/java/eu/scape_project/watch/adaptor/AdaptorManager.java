package eu.scape_project.watch.adaptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.PluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * The AdaptorManager is responsible for the known SourceAdaptors (provenance
 * information) and the loaded adaptors that are currently used in the system.
 * It keeps a reference to them and is able to shut them down accordingly, when
 * the time is right. It provides facilities for obtaining a fully instantiated
 * and preconfigured adaptor instance.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class AdaptorManager {

  /**
   * Default logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(AdaptorManager.class);

  /**
   * The known source adaptors.
   */
  private Map<String, SourceAdaptor> adaptors;

  /**
   * All running adaptors with their corresponding instance id.
   */
  private Map<String, AdaptorPluginInterface> cached;

  /**
   * Reverse cache to get adaptors from adaptor plugin interface.
   */
  private Map<AdaptorPluginInterface, String> reverseCached;

  /**
   * Creates a new adaptor manager and loads the known source adaptors.
   */
  public AdaptorManager() {
    LOG.info("Creating adaptor manager");
    this.adaptors = new HashMap<String, SourceAdaptor>();
    this.cached = new HashMap<String, AdaptorPluginInterface>();
    this.reverseCached = new HashMap<AdaptorPluginInterface, String>();
    reloadKnownAdaptors();
  }

  /**
   * This method loads the known source adaptors from the knowledge base.
   */
  public synchronized void reloadKnownAdaptors() {
    this.adaptors.clear();
    this.shutdownAll();

    final int count = DAO.SOURCE_ADAPTOR.countAll();
    final List<SourceAdaptor> all = DAO.SOURCE_ADAPTOR.query("", 0, count);
    LOG.info("Found {} source adaptors", all.size());

    for (SourceAdaptor sa : all) {
      this.adaptors.put(sa.getInstance(), sa);

      if (sa.isActive()) {
        final String instance = sa.getInstance();
        LOG.info("Found active adaptor with instance id [{}], reloading", instance);
        final AdaptorPluginInterface plugin = this.getAdaptorInstance(instance);

        if (plugin == null) {
          LOG.error("An error occurred while reloading the active adaptor with instance [{}]", instance);
        }
      }
    }
  }

  /**
   * Creates a {@link SourceAdaptor}. Note that the method checks, whether an
   * implementation of such an adaptor exists. If no implementation is found
   * than the method returns null, otherwise a {@link SourceAdaptor} object is
   * returned.
   * 
   * @param name
   *          the name of the adaptor.
   * @param version
   *          the version of the adaptor.
   * @param uid
   *          the unique instance identifier provided by the user.
   * @param source
   *          the source to which the adaptor is going to be used against.
   * @param configuration
   * @return the source adaptor without any configuration or null if no
   *         implementation was found..
   */
  public SourceAdaptor createAdaptor(final String name, final String version, final String uid,
    final Map<String, String> configuration, final Source source) {
    LOG.debug("Creating new source adaptor information for {}-{}", name, version);

    final PluginInfo pluginInfo = PluginManager.getDefaultPluginManager().getPluginInfo(name, version);

    SourceAdaptor adaptor = null;

    // existence check
    if (pluginInfo != null && pluginInfo.getType().equals(PluginType.ADAPTOR)) {

      // TODO get types and properties from adaptorPlugin, blocked by #120.
      final List<EntityType> types = null;
      final List<Property> properties = null;

      adaptor = new SourceAdaptor(name, version, uid, source, types, properties, configuration);
      this.updateSourceAdaptor(adaptor);
      this.adaptors.put(adaptor.getInstance(), adaptor);

    } else {
      LOG.warn("Cannot create source adaptor. No plugin implementation found for [{}-{}].", name, version);
    }

    return adaptor;
  }

  /**
   * Creates a {@link SourceAdaptor}.
   * 
   * @param info
   *          the plugin info.
   * @param uid
   *          the unique instance id.
   * @param source
   *          the source for the source adaptor.
   * @return the source adaptor.
   */
  public SourceAdaptor createAdaptor(final PluginInfo info, final String uid, final Map<String, String> configuration,
    final Source source) {
    return this.createAdaptor(info.getName(), info.getVersion(), uid, configuration, source);
  }

  /**
   * Updates the source adaptor. This method shall be used if the configuration
   * changes.
   * 
   * @param adaptor
   *          the adaptor to update.
   */
  public void updateSourceAdaptor(final SourceAdaptor adaptor) {
    LOG.debug("Updating SourceAdaptor Information {}-{}", adaptor.getName(), adaptor.getVersion());
    DAO.save(adaptor);
    this.adaptors.put(adaptor.getInstance(), adaptor);
  }

  /**
   * Gets the source adaptor for this instance, or null.
   * 
   * @param instance
   *          the instance identifier.
   * @return the SourceAdaptor.
   */
  public SourceAdaptor getSourceAdaptor(final String instance) {
    return this.adaptors.get(instance);
  }

  // TODO extend the adaptorplugininterface and add supported types.
  // TODO create similar methods for the types...
  /**
   * Provides the needed configuration parameters for an adaptor with the given
   * name and version. Based on these the user can configure the adaptor
   * accordingly and update it.
   * 
   * @param name
   *          the name of the adaptor.
   * @param version
   *          the version of the adaptor.
   * @return a list with {@link ConfigParameter} objects.
   */
  public List<ConfigParameter> getConfigurationParameters(final String name, final String version) {
    LOG.debug("Retrieving the supported configuration parameters of: {}-{}", name, version);
    final AdaptorPluginInterface plugin = this.getPlugin(name, version);

    if (plugin != null) {
      return plugin.getParameters();
    }

    return null;
  }

  /**
   * Retrieves a read-only map of the active adaptor plugins. The key is the
   * instance of the adaptor and the value is the adaptor plugin.
   * 
   * @return the active plugins.
   */
  public Map<String, AdaptorPluginInterface> getActiveAdaptorPlugins() {
    return Collections.unmodifiableMap(this.cached);
  }

  /**
   * Instantiates, initializes and configures an adaptor implementation ready to
   * fetch data. If the passed instance is not associated with any adaptor then
   * null is returned. If the adaptor is already running, then the cached
   * instance is used. The method makes sure that the init method is called only
   * once for each instance, and that the parameter values are applied each time
   * when the adaptor instance is retrieved.
   * 
   * @param instance
   *          the identifier of the source adaptor instance supplied by the
   *          user.
   * @return the {@link AdaptorPluginInterface} implementation or null.
   */
  public AdaptorPluginInterface getAdaptorInstance(final String instance) {
    final SourceAdaptor adaptor = this.getSourceAdaptor(instance);
    AdaptorPluginInterface plugin = null;

    if (adaptor == null) {
      LOG.warn("No adaptor found with instance id: {}", instance);
    } else {
      LOG.debug("Looking for implementation in cache: {}-{}", adaptor.getName(), adaptor.getVersion());
      plugin = this.cached.get(instance);
      boolean initialized = true;

      if (plugin == null) {
        plugin = this.getPlugin(adaptor.getName(), adaptor.getVersion());
        initialized = false;
      }

      if (plugin != null && !initialized) {
        try {
          plugin.init();
          this.cached.put(instance, plugin);
          this.reverseCached.put(plugin, instance);
        } catch (final PluginException e) {
          LOG.error("An error occurred during plugin initialization: {}", e.getMessage());
        }
      }

      if (plugin != null) {
        try {
          plugin.setParameterValues(adaptor.getConfigurationAsMap());
          adaptor.setActive(true);
          this.updateSourceAdaptor(adaptor);
        } catch (final InvalidParameterException e) {
          LOG.error("An error occurred during cached plugin configuration: {}", e.getMessage());
          LOG.info("Disabling plugin with instance id: " + instance);
          adaptor.setActive(false);
          this.updateSourceAdaptor(adaptor);
        }
      }
    }
    return plugin;
  }

  public SourceAdaptor getSourceAdaptor(final AdaptorPluginInterface adaptorPlugin) {
    SourceAdaptor sourceAdaptor = null;
    String instance = reverseCached.get(adaptorPlugin);
    if (instance != null) {
      sourceAdaptor = getSourceAdaptor(instance);
    }
    return sourceAdaptor;
  }

  /**
   * Shuts down the plugin with the specified id if it is existing and sets the
   * corresponding adaptor as inactive.
   * 
   * @param instance
   *          the instance id of the source adaptor.
   */
  public void shutdown(final String instance) {
    LOG.info("Trying to shutdown plugin with id: {}", instance);
    final SourceAdaptor adaptor = this.getSourceAdaptor(instance);
    final AdaptorPluginInterface plugin = this.cached.remove(instance);
    this.reverseCached.remove(plugin);

    this.shutdown(plugin);

    adaptor.setActive(false);
    this.updateSourceAdaptor(adaptor);
  }

  /**
   * Shuts down all running plugins and removes the from the cache.
   * 
   * @see AdaptorManager#shutdown(String)
   */
  public void shutdownAll() {
    LOG.info("Shutting down all plugins");
    for (final String id : this.cached.keySet()) {
      final AdaptorPluginInterface plugin = this.cached.remove(id);
      this.reverseCached.remove(plugin);
      this.shutdown(plugin);
    }
  }

  /**
   * Shutdowns a single plugin if it is not null.
   * 
   * @param plugin
   *          the plugin to shut down.
   */
  private void shutdown(final AdaptorPluginInterface plugin) {
    if (plugin == null) {
      LOG.warn("Passed plugin is null, skipping shutdown");
    } else {
      LOG.info("Shutting down plugin: {}-{}", plugin.getName(), plugin.getVersion());
      try {
        plugin.shutdown();
      } catch (final PluginException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Retrieves a plugin instance via the {@link PluginManager} for the
   * corresponding name and version or null if none was found.
   * 
   * @param name
   *          the name of the plugin.
   * @param version
   *          the version of the plugin.
   * @return the plugin implementation.
   */
  private AdaptorPluginInterface getPlugin(final String name, final String version) {
    LOG.debug("Retrieving the implementation of: {}-{}", name, version);
    final PluginManager pm = PluginManager.getDefaultPluginManager();
    final List<PluginInfo> info = pm.getPluginInfo(name);

    AdaptorPluginInterface plugin = null;
    for (PluginInfo pi : info) {
      if (pi.getVersion().equals(version)) {
        plugin = (AdaptorPluginInterface) pm.getPlugin(pi.getClassName(), pi.getVersion());
        break;
      }
    }

    return plugin;
  }

}
