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
 * @author Scout Dev Team
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
  public static final String KB_DATA_FOLDER_KEY = "scout.kb.data";

  /**
   * The property designating whether or not the test data should be inserted on
   * startup.
   */
  public static final String KB_INSERT_TEST_DATA = "scout.kb.insert_test_data";

  /**
   * The property designating the folder where the plugins are located.
   */
  public static final String PLUGINS_DIRECTORY_KEY = "scout.plugins";

  /**
   * Name of the folder where all scout configurations will be located.
   */
  public static final String PROPERTIES_FOLDER_NAME = "scout";

  /**
   * Name of primary scout configuration file.
   */
  public static final String PROPERTIES_FILE_NAME = "scout.properties";

  /**
   * The default properties path inside of the package.
   */
  private static final String DEFAULT_PROPERTIES = "config/" + PROPERTIES_FILE_NAME;

  /**
   * The current user properties path, currently in the users home.
   */
  private static final String USER_PROPERTIES = System.getProperty("user.home") + File.separator + "."
    + PROPERTIES_FOLDER_NAME + File.separator + PROPERTIES_FILE_NAME;

  /**
   * The global properties path.
   * 
   * TODO support windows system properties path
   */
  private static final String SYSTEM_PROPERTIES = "/etc/" + PROPERTIES_FOLDER_NAME + File.separator
    + PROPERTIES_FILE_NAME;

  /**
   * The identifier of the default config provided with the system.
   */
  public static final int DEFAULT_CONFIG = 0x01;

  /**
   * The identifier of the user config.
   */
  public static final int USER_CONFIG = 0x02;

  /**
   * The identifier of the system wide config.
   */
  public static final int SYSTEM_CONFIG = 0x03;

  /**
   * The loaded properties.
   */
  private Properties config;

  /**
   * The sub component that uses this configuration.
   */
  private String module;

  /**
   * The default constructor loads the properties.
   * 
   * @param module
   *          the name of the sub component that is using this instance of the
   *          configuration.
   * @see {@link ConfigUtils#loadConfig()}
   */
  public ConfigUtils(final String module) {
    LOGGER.info("Subcomponent [{}] has created new configuration instance", module);

    this.module = module;
    loadConfig();
  }

  /**
   * First tries to load the user config from the home directory. If no config
   * is found it tries to load the global config and if no global config is
   * found the method falls back to the default config.
   */
  private void loadConfig() {
    if (!loadUserConfig() && !loadSystemWideConfig()) {
      loadDefaultConfig();
    }
  }

  /**
   * Loads the default properties.
   */
  private void loadDefaultConfig() {
    LOGGER.info("Subcomponent [{}] is loading default config file: {}", this.module, DEFAULT_PROPERTIES);
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
      LOGGER.info("Subcomponent [{}] is loading user defined config file: {}.", this.module, USER_PROPERTIES);
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
      LOGGER.info("Subcomponent [{}] is loading system wide config file: {}.", this.module, SYSTEM_PROPERTIES);

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
   * Overrides the currently loaded configuration with the specified one. If
   * some component is already using a configuration then it is not the
   * responsibility of this class to apply the newly loaded configuration.
   * 
   * If the passed parameter is not one of the possible options, then the
   * default configuration is loaded.
   * 
   * @param cnf
   *          the desired configuration.
   * 
   * @see ConfigUtils#DEFAULT_CONFIG
   * @see ConfigUtils#USER_CONFIG
   * @see ConfigUtils#SYSTEM_CONFIG
   */
  public void override(final int cnf) {
    LOGGER.info("Overriding configs with: {}", cnf);
    switch (cnf) {
      case DEFAULT_CONFIG:
        this.loadDefaultConfig();
        break;
      case USER_CONFIG:
        this.loadUserConfig();
        break;
      case SYSTEM_CONFIG:
        this.loadSystemWideConfig();
        break;

      default:
        this.loadConfig();
    }
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
