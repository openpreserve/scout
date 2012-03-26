package eu.scape_project.watch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple configuration utilities class, that loads the configuration files
 * into memory and allows the retrieval of some config parameters. It first
 * looks into the home directory for a config file, if none is found it falls
 * back to the /etc/ directory and if none is found again it takes the default
 * properties.
 * 
 * @author Watch Dev Team
 * 
 */
public class ConfigUtils {

  /**
   * A default logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);

  /**
   * The property designating the data folder of the watch tdb.
   */
  public static final String KB_DATA_FOLDER_KEY = "watch.kb.data_folder";

  /**
   * The property designating whether or not the test data should be inserted on
   * startup.
   */
  public static final String KB_INSERT_TEST_DATA = "watch.kb.test_data";

  /**
   * The default properties path inside of the package.
   */
  private static final String DEFAULT_PROPERTIES = "config/watchconfig.properties";

  /**
   * The current user properties path, currently in the users home.
   */
  private static final String USER_PROPERTIES = System.getProperty("user.home") + "/.watchconfig";

  /**
   * The global properties path.
   */
  private static final String SYSTEM_PROPERTIES = "/etc/watchconfig";

  /**
   * The loaded properties.
   */
  private Properties config;

  /**
   * The default constructor loads the properties.
   * 
   * @see {@link ConfigUtils#loadConfig()}
   */
  public ConfigUtils() {
    loadConfig();
  }

  /**
   * First tries to load the user config from the home directory. If no config
   * is found it tries to load the global config and if no global config is
   * found the method falls back to the default config.
   */
  private void loadConfig() {
    if (!loadUserConfig()) {
      if (!loadSystemWideConfig()) {
        loadDefaultConfig();
      }
    }

  }

  /**
   * Loads the default properties.
   */
  private void loadDefaultConfig() {
    LOGGER.debug("loading default config file: {}", DEFAULT_PROPERTIES);
    this.config = new Properties();
    try {
      this.config.load(ConfigUtils.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES));
    } catch (final IOException e) {
      LOGGER.error("Default config file not found! {}", e.getMessage());
    }
  }

  /**
   * Loads the properties specified in the user home directory and returns true
   * if the operation was successful or false otherwise.
   * 
   * @return true if the properties were loaded or false otherwise.
   */
  private boolean loadUserConfig() {
    LOGGER.debug("lookig for user defined config file: {}", USER_PROPERTIES);

    final File f = new File(USER_PROPERTIES);

    if (f.exists() && f.isFile()) {
      LOGGER.debug("found user defined properties, loading.");
      FileInputStream stream = null;
      try {
        stream = new FileInputStream(f);
        this.config = new Properties();
        this.config.load(stream);

        return true;

      } catch (final IOException e) {
        LOGGER.warn("Could not load user defined properties file '{}', cause: {}", USER_PROPERTIES, e.getMessage());
      } finally {
        try {
          if (stream != null) {
            stream.close();
          }
        } catch (final IOException e) {
          LOGGER.warn("Could not close the stream in a clean fashion: {}", e.getMessage());
        }
      }

    } else {
      LOGGER.debug("User defined config file was not found.");
    }

    return false;
  }

  /**
   * Loads the system wide properties and returns true if the operation was
   * successful or false otherwise.
   * 
   * @return true if the properties were loaded or false otherwise.
   */
  private boolean loadSystemWideConfig() {
    LOGGER.debug("lookig for system wide config file: {}", SYSTEM_PROPERTIES);

    final File f = new File(SYSTEM_PROPERTIES);

    if (f.exists() && f.isFile()) {
      LOGGER.debug("found system wide properties, loading.");

      FileInputStream stream = null;

      try {
        stream = new FileInputStream(f);
        this.config = new Properties();
        this.config.load(stream);

        return true;

      } catch (final IOException e) {
        LOGGER.warn("Could not load system wide properties file '{}', cause: {}", SYSTEM_PROPERTIES, e.getMessage());
      } finally {
        if (stream != null) {
          try {
            stream.close();
          } catch (final IOException e) {
            LOGGER.warn("Could not close the stream in a clean fashion: {}", e.getMessage());
          }
        }
      }
    } else {
      LOGGER.debug("System wide config file was not found.");
    }

    return false;
  }

  /**
   * Gets a String representation for the property key or an empty string if no
   * property was found.
   * 
   * @param key
   *          the key of the property
   * @return a string with the value or an empty string if none found.
   */
  public String getStringProperty(final String key) {
    return this.config.getProperty(key, "");
  }

  /**
   * Returns an integer value for the specified property key or -1.
   * 
   * @param key
   *          the key of the property.
   * @return the value or -1.
   */
  public int getIntProperty(final String key) {
    return Integer.parseInt(this.config.getProperty(key, "-1"));
  }

  /**
   * Returns a boolean value for the specified property key or false if none was
   * found.
   * 
   * @param key
   *          the key of the property.
   * @return the value or false.
   */
  public boolean getBooleanProperty(final String key) {
    return Boolean.valueOf(this.config.getProperty(key, "false"));
  }

}
