package eu.scape_project.watch.core.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Thanks to Luis Faria and Rui Castro for the reference implementation.
 */
/**
 * This is the watch plugin manager. It is responsible for loading
 * {@link Plugin}s.
 * 
 * @author Petar Petrov - <me@petarpetrov.org>
 */
public final class PluginManager {

  /**
   * A default logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

  /**
   * The default scanning period, currently set to 30 seconds.
   */
  private static final long SCANNER_PERIOD = 30L * 1000;
  /**
   * The default Plugin Manager instance.
   */
  private static PluginManager defaultPluginManager = null;

  /**
   * Gets the default {@link PluginManager}.
   * 
   * @return the default {@link PluginManager}.
   */
  public static synchronized PluginManager getDefaultPluginManager() {
    if (PluginManager.defaultPluginManager == null) {
      PluginManager.defaultPluginManager = new PluginManager();
    }
    return PluginManager.defaultPluginManager;
  }

  /**
   * The directory of the plugins.
   */
  private File pluginsDirectory = null;

  /**
   * The scanner plugin timer.
   */
  private Timer scannerTimer = null;

  /**
   * The plugin registry.
   */
  private Map<File, JarPlugin> pluginRegistry = new HashMap<File, JarPlugin>();

  /**
   * Returns all {@link Plugin}s present in all jars.
   * 
   * @return a {@link List} of {@link Plugin}s.
   */
  public List<Plugin> getPlugins() {
    final List<Plugin> plugins = new ArrayList<Plugin>();

    for (JarPlugin jarPlugin : this.pluginRegistry.values()) {
      if (jarPlugin.plugin != null) {
        plugins.add(jarPlugin.plugin);
      }
    }
    return plugins;
  }

  // /**
  // * Returns the {@link PluginInfo}s for all {@link Plugin}s.
  // *
  // * @return a {@link List} of {@link PluginInfo}s.
  // */
  // public List<PluginInfo> getPluginsInfo() {
  // List<PluginInfo> pluginsInfo = new ArrayList<PluginInfo>();
  //
  // for (Plugin plugin : getPlugins()) {
  // pluginsInfo.add(getPluginInfo(plugin));
  // }
  //
  // return pluginsInfo;
  // }

  /**
   * Returns an instance of the {@link Plugin} with the specified ID
   * (classname).
   * 
   * @param pluginID
   *          the ID (classname) of the {@link Plugin}.
   * 
   * @return a {@link Plugin} or <code>null</code> if the specified classname if
   *         not a {@link Plugin}.
   */
  public Plugin getPlugin(final String pluginID) {
    Plugin plugin = null;
    for (JarPlugin jarPlugin : this.pluginRegistry.values()) {
      if (jarPlugin.plugin != null && jarPlugin.plugin.getClass().getName().equals(pluginID)) {
        plugin = jarPlugin.plugin;
        break;
      }
    }
    return plugin;
  }

  // /**
  // * @param pluginID
  // *
  // * @return {@link PluginInfo} or <code>null</code>.
  // */
  // public PluginInfo getPluginInfo(String pluginID) {
  // Plugin plugin = getPlugin(pluginID);
  // if (plugin != null) {
  // return getPluginInfo(plugin);
  // } else {
  // return null;
  // }
  // }

  /**
   * This method should be called to stop {@link PluginManager} and all
   * {@link Plugin}s currently loaded.
   */
  public void shutdown() {

    if (this.scannerTimer != null) {
      // Stop the plugin loader timer
      this.scannerTimer.cancel();
    }

    for (JarPlugin jarPlugin : this.pluginRegistry.values()) {
      if (jarPlugin.plugin != null) {
        try {
          jarPlugin.plugin.shutdown();
        } catch (final PluginException e) {
          LOGGER.warn("Plugin {} did not perform a clean shutdown: ", e.getMessage());
        }
      }
    }
  }

  /**
   * Constructs a new {@link PluginManager}.
   * 
   * 
   */
  private PluginManager() {
    try {
      final Properties configuration = getConfiguration("plugins.properties");
      final String dir = configuration.getProperty("pluginsDirectory");
      final File pluginDir = new File(dir);

      LOGGER.debug("Plugin directory is " + pluginDir);

      this.setPluginDirectory(pluginDir);
    } catch (final IOException e) {
      LOGGER.debug("Error reading plugins.properties - " + e.getMessage(), e);
    }

    LOGGER.debug("Starting plugin scanner timer...");

    this.scannerTimer = new Timer("Plugin scanner timer", true);
    this.scannerTimer.schedule(new SearchPluginsTask(), new Date(), SCANNER_PERIOD);

    LOGGER.info(getClass().getSimpleName() + " init OK");
  }

  /**
   * Return the configuration properties with the specified name.
   * 
   * @param configFile
   *          the name of the configuration file.
   * 
   * @return a {@link Properties} with the properties of the specified name.
   * 
   * @throws IOException
   *           if the config file cannot be loaded.
   */
  private Properties getConfiguration(final String configFile) throws IOException {
    LOGGER.info("Loading default configuration " + configFile);

    final Properties config = new Properties();
    config.load(new FileInputStream(configFile));

    return config;
  }

  /**
   * @return the pluginDirectory
   */
  private File getPluginDirectory() {
    return this.pluginsDirectory;
  }

  /**
   * @param pluginDirectory
   *          the pluginDirectory to set
   * 
   * @throws IllegalArgumentException
   *           if pluginDirectory is null or not a directory.
   */
  private void setPluginDirectory(final File pluginDirectory) {

    if (pluginDirectory == null) {
      throw new IllegalArgumentException("pluginDirectory cannot be null");
    } else if (!pluginDirectory.isDirectory()) {
      throw new IllegalArgumentException("pluginDirectory " + pluginDirectory + " is not a directory.");
    } else {
      this.pluginsDirectory = pluginDirectory;
    }
  }

  // private PluginInfo getPluginInfo(Plugin plugin) {
  // List<PluginParameter> parameters = plugin.getParameters();
  // return new PluginInfo(plugin.getClass().getName(), plugin.getName(),
  // plugin.getVersion(), plugin.getDescription(),
  // parameters.toArray(new PluginParameter[parameters.size()]));
  // }

  /**
   * Checks if there are any new jars in the plugin directory or if any of the
   * old ones has a new last modified date and loads all new plugins.
   */
  private void loadPlugins() {

    final File[] jarFiles = this.getPluginDirectory().listFiles(new FileFilter() {
      public boolean accept(final File pathname) {
        return pathname.getName().endsWith(".jar");
      }
    });

    final URL[] jarURLs = new URL[jarFiles.length];
    for (int i = 0; i < jarFiles.length; i++) {
      try {
        jarURLs[i] = jarFiles[i].toURI().toURL();
      } catch (final MalformedURLException e) {
        LOGGER.warn("Error getting jar file URL - " + e.getMessage(), e);
      }
    }

    for (File jarFile : jarFiles) {
      if (this.pluginRegistry.containsKey(jarFile) && jarFile.lastModified() == this.pluginRegistry.get(jarFile).lastModified) {
        // The plugin already exists
        LOGGER.debug(jarFile.getName() + " is already loaded");
      } else {
        // The plugin doesn't exist or the modification date is
        // different. Let's load the Plugin
        LOGGER.debug(jarFile.getName() + " is not loaded or modification dates differ. Inspecting Jar...");

        final Plugin plugin = loadPlugin(jarFile, jarURLs);

        try {
          if (plugin != null) {

            plugin.init();
            LOGGER.debug("Plugin started " + plugin.getName() + " (version " + plugin.getVersion() + ")");

          } else {

            LOGGER.trace(jarFile.getName() + " is not a Plugin");

          }

          synchronized (this.pluginRegistry) {
            this.pluginRegistry.put(jarFile, new JarPlugin(plugin, jarFile.lastModified()));
          }

        } catch (final PluginException e) {
          LOGGER.error("Plugin failed to initialize", e);
        }
      }

    }

  }

  /**
   * Loads a plugin and all its classes in the jar.
   * 
   * @param jarFile
   *          the jar file containing the plugin class.
   * @param jarURLs
   *          the jar file urls in the plugin directory.
   * @return the plugin or null, if no plugin was found.
   */
  private Plugin loadPlugin(final File jarFile, final URL[] jarURLs) {

    JarFile jar = null;
    Plugin plugin = null;

    try {
      jar = new JarFile(jarFile);
    } catch (final IOException e) {
      LOGGER.error("Could not open jar file: {}", e.getMessage());
      return plugin;
    }

    final Enumeration<JarEntry> entries = jar.entries();
    final URLClassLoader loader = new URLClassLoader(jarURLs, PluginManager.class.getClassLoader());

    while (entries.hasMoreElements()) {
      final JarEntry entry = entries.nextElement();
      if (entry.getName().endsWith(".class")) {
        final String className = entry.getName().replaceAll("/", ".").replaceAll(".class", "");
        LOGGER.trace("Found class: {}, trying to load it", className);
        try {

          final Class<?> clazz = loader.loadClass(className);
          if (Plugin.class.isAssignableFrom(clazz) && !clazz.isInterface()
            && !Modifier.isAbstract(clazz.getModifiers())) {
            LOGGER.debug("class is a plugin, instantiating");
            plugin = (Plugin) clazz.newInstance();
          }
        } catch (final ClassNotFoundException e) {
          LOGGER.error("Class Not Found {}: {}", className, e.getMessage());
        } catch (final InstantiationException e) {
          LOGGER.error("Could not instantiate class {}: {}", className, e.getMessage());
        } catch (final IllegalAccessException e) {
          LOGGER.error("Illegal Access on class {}: {} ", className, e.getMessage());
        } finally {
          if (jar != null) {
            try {
              jar.close();
            } catch (final IOException e) {
              LOGGER.warn("Could not close the jar: {}", e.getMessage());
            }
          }
        }
      }
    }

    return plugin;
  }

  /**
   * The task that loads the plugins and adds them to the registry.
   * 
   * @author Petar Petrov <me@petarpetrov.org>
   * 
   */
  class SearchPluginsTask extends TimerTask {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

      LOGGER.debug("Searching for plugins...");

      PluginManager.this.loadPlugins();

      LOGGER.debug("Search complete - " + PluginManager.this.pluginRegistry.size() + " jar files");

      for (File jarFile : PluginManager.this.pluginRegistry.keySet()) {

        final Plugin plugin = PluginManager.this.pluginRegistry.get(jarFile).plugin;

        if (plugin != null) {
          LOGGER.debug("- " + jarFile.getName());
          LOGGER.debug("--- " + plugin.getName() + "-" + plugin.getVersion());
        }
      }
    }
  }

  /**
   * A helper wrapper class.
   * 
   * @author Petar Petrov <me@petarpetrov.org>
   * 
   */
  class JarPlugin {

    /**
     * The plugin.
     */
    private Plugin plugin = null;
    /**
     * The last modified date of this plugin.
     */
    private long lastModified = 0;

    /**
     * Initializes a JarPlugin.
     * 
     * @param plugin
     *          the plugin to wrap.
     * @param lastModified
     *          the last modified date.
     */
    JarPlugin(final Plugin plugin, final long lastModified) {
      this.plugin = plugin;
      this.lastModified = lastModified;
    }
  }

}
