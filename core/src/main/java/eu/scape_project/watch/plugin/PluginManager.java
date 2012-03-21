package eu.scape_project.watch.plugin;

import java.io.File;
import java.io.FileFilter;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import eu.scape_project.watch.interfaces.PluginInterface;
import eu.scape_project.watch.utils.ConfigUtils;
import eu.scape_project.watch.utils.exceptions.PluginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Thanks to Luis Faria and Rui Castro for the reference implementation.
 */
/**
 * This is the watch plugin manager. It is responsible for loading
 * {@link PluginInterface}s. Currently it loads all jar files in a pre defined location
 * and builds an internal registry of all {@link PluginInterface} classes in there. As
 * soon as the {@link PluginManager#getPlugin(String, String)} method is called
 * a new plugin is created and its init method is called. Calling the shutdown
 * method of the plugin is the responsibility of the user of the PluginInterface.
 * 
 * @author Petar Petrov - <me@petarpetrov.org>
 */
public final class PluginManager {

  /**
   * A default logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

  /**
   * The default scanning period, currently set to 60 seconds.
   */
  private static final long SCANNER_PERIOD = 60L * 1000;

  /**
   * A helper string for .class.
   */
  private static final String CLASS_EXTENSION = ".class";

  /**
   * The default PluginInterface Manager instance.
   */
  private static PluginManager defaultPluginManager = null;

  /**
   * Gets the default {@link PluginManager}.
   * 
   * @return the default {@link PluginManager}.
   */
  public static synchronized PluginManager getDefaultPluginManager() {
    if (PluginManager.defaultPluginManager == null) {
      LOGGER.debug("Craeting new default instance of the plugin manager");
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
   * The loaded plugins.
   */
  private List<PluginInterface> pluginCache = new ArrayList<PluginInterface>();

  /**
   * Returns an initialized instance of the {@link PluginInterface} with the specified ID
   * (classname) or null if the initializing process failed.
   * 
   * @param pluginID
   *          the ID (classname) of the {@link PluginInterface}.
   * @param version
   *          the version of the plugin, as there might be some ambiguities.
   * 
   * @return a {@link PluginInterface} or <code>null</code> if the specified classname if
   *         not a {@link PluginInterface}.
   */
  public PluginInterface getPlugin(final String pluginID, final String version) {
    PluginInterface plugin = null;
    for (JarPlugin jarPlugin : this.pluginRegistry.values()) {
      final PluginInterface tmp = jarPlugin.plugin;
      if (tmp != null && tmp.getClass().getName().equals(pluginID) && tmp.getVersion().equals(version)) {
        plugin = this.createInstance(jarPlugin.plugin.getClass());
        break;
      }
    }

    if (plugin != null) {
      try {
        plugin.init();
        this.pluginCache.add(plugin);
      } catch (final PluginException e) {
        // TODO what shall we do here..
        LOGGER.warn("An error occurred during plugin initialization: {}", e.getMessage());
        return null;
      }
    }
    return plugin;
  }

  /**
   * Retrieves info for all plugins currently in the registry.
   * 
   * @return a list of {@link PluginInfo}s
   */
  public List<PluginInfo> getPluginInfo() {
    return this.getPluginInfo(null);
  }

  /**
   * Obtain plugin information about all plugins of the given type.
   * 
   * @param type
   *          the type of the PluginInterface
   * @return a list with {@link PluginInfo}s
   * @see {@link PluginType}
   */
  public List<PluginInfo> getPluginInfo(final PluginType type) {
    final List<PluginInfo> info = new ArrayList<PluginInfo>();

    if (type == null) {
      for (JarPlugin jp : this.pluginRegistry.values()) {
        final PluginInterface p = jp.plugin;
        info.add(new PluginInfo(p.getName(), p.getVersion(), p.getDescription(), p.getClass().getName()));
      }
    } else {
      for (JarPlugin jp : this.pluginRegistry.values()) {
        final PluginInterface p = jp.plugin;
        if (p.getPluginType() == type) {
          info.add(new PluginInfo(p.getName(), p.getVersion(), p.getDescription(), p.getClass().getName()));
        }
      }
    }

    return info;
  }

  /**
   * Rescans the plugins folder on demand.
   */
  public void reScan() {
    this.startTimer();
  }

  /**
   * This method should be called to stop {@link PluginManager} and all
   * {@link PluginInterface}s currently loaded.
   */
  public synchronized void shutdown() {
    LOGGER.info("Shutting down plugin manager");

    this.cancelTimer();

    for (PluginInterface p : this.pluginCache) {
      try {
        p.shutdown();
      } catch (final PluginException e) {
        LOGGER.warn("PluginInterface {} did not shutdown correctly: {}", p.getName(), e.getMessage());
      }
    }

    this.pluginCache.clear();
    this.pluginRegistry.clear();
    PluginManager.defaultPluginManager = null;
  }

  /**
   * Constructs a new {@link PluginManager}.
   * 
   * 
   */
  private PluginManager() {
    final ConfigUtils config = new ConfigUtils();
    final String dir = config.getStringProperty("watch.plugins.directory");
    final File pluginDir = new File(dir);

    LOGGER.debug("PluginInterface directory is " + pluginDir);

    this.setPluginDirectory(pluginDir);

    LOGGER.debug("Starting plugin scanner timer...");
    this.startTimer();

    LOGGER.info(getClass().getSimpleName() + " is started");
  }

  /**
   * Cancels the timer if it is already running, and starts it again. The task
   * is executed immediately and then every 60 seconds (per default).
   */
  private void startTimer() {
    this.cancelTimer();
    this.scannerTimer = new Timer("PluginInterface scanner timer", true);
    this.scannerTimer.schedule(new SearchPluginsTask(), new Date(), SCANNER_PERIOD);
  }

  /**
   * Stops the timer task if there is one.
   */
  private void cancelTimer() {
    if (this.scannerTimer != null) {
      this.scannerTimer.cancel();
    }
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

    for (File jarFile : jarFiles) {
      if (this.pluginRegistry.containsKey(jarFile)
        && jarFile.lastModified() == this.pluginRegistry.get(jarFile).lastModified) {
        // The plugin already exists
        LOGGER.debug(jarFile.getName() + " is already loaded");
      } else {
        // The plugin doesn't exist or the modification date is
        // different. Let's load the PluginInterface
        LOGGER.debug(jarFile.getName() + " is not loaded or modification dates differ. Inspecting Jar...");

        try {
          final URL[] urls = {jarFile.toURI().toURL()};

          final PluginInterface plugin = loadPlugin(jarFile, urls);

          if (plugin == null) {
            LOGGER.trace(jarFile.getName() + " is not a PluginInterface");
          }

          synchronized (this.pluginRegistry) {
            this.pluginRegistry.put(jarFile, new JarPlugin(plugin, jarFile.lastModified()));
          }
        } catch (final MalformedURLException e) {
          e.printStackTrace();
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
  private PluginInterface loadPlugin(final File jarFile, final URL[] jarURLs) {

    JarFile jar = null;
    PluginInterface plugin = null;

    try {
      jar = new JarFile(jarFile);
    } catch (final IOException e) {
      LOGGER.error("Could not open jar file: {}", e.getMessage());
      return plugin;
    }

    final Enumeration<JarEntry> entries = jar.entries();
    final URLClassLoader loader = URLClassLoader.newInstance(jarURLs, PluginManager.class.getClassLoader());

    LOGGER.debug("Looking inside jar file: {}", jarFile);
    while (entries.hasMoreElements()) {
      final JarEntry entry = entries.nextElement();
      if (entry.getName().endsWith(CLASS_EXTENSION)) {
        final String className = entry.getName().replaceAll("/", ".").replaceAll(CLASS_EXTENSION, "");
        LOGGER.debug("Found class: {}, trying to load it", className);
        try {
          final Class<?> clazz = loader.loadClass(className);
          LOGGER.debug("Loaded {}", className);
          final PluginInterface tmp = this.createInstance(clazz);
          if (tmp != null) {
            // set the plugin only if it was really loaded
            // otherwise continue to load classes.
            plugin = tmp;
            LOGGER.debug("PLugin instantiated");
          }
        } catch (final ClassNotFoundException e) {
          LOGGER.error("Class Not Found {}: {}", className, e.getMessage());
        }
      }
    }

    if (jar != null) {
      try {
        jar.close();
      } catch (final IOException e) {
        LOGGER.warn("Could not close the jar: {}", e.getMessage());
      }
    }

    return plugin;
  }

  /**
   * Creates a plugin instance if the class is of a plugin type.
   * 
   * @param clazz
   *          the class of the plugin.
   * @return the plugin or null.
   */
  private PluginInterface createInstance(final Class<?> clazz) {
    PluginInterface plugin = null;

    try {
      if (PluginInterface.class.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
        LOGGER.debug("class is a plugin, instantiating");
        plugin = (PluginInterface) clazz.newInstance();
      }

    } catch (final InstantiationException e) {
      LOGGER.error("Could not instantiate class {}: {}", clazz.getName(), e.getMessage());
    } catch (final IllegalAccessException e) {
      LOGGER.error("Illegal Access on class {}: {} ", clazz.getName(), e.getMessage());
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

        final PluginInterface plugin = PluginManager.this.pluginRegistry.get(jarFile).plugin;

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
    private PluginInterface plugin = null;
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
    JarPlugin(final PluginInterface plugin, final long lastModified) {
      this.plugin = plugin;
      this.lastModified = lastModified;
    }
  }

}
