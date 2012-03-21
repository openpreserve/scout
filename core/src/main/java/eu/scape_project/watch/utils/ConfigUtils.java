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

  public static final String KB_DATA_FOLDER_KEY = "watch.kb.data_folder";

  public static final String KB_INSERT_TEST_DATA = "watch.kb.test_data";

  private static final String DEFAULT_PROPERTIES = "config/watchconfig.properties";

  private static final String USER_PROPERTIES = System.getProperty("user.home") + "/.watchconfig";

  private static final String SYSTEM_PROPERTIES = "/etc/watchconfig";

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);


  private Properties config;

  public ConfigUtils() {
    loadConfig();
  }

  private void loadConfig() {
    if (!loadUserConfig()) {
      if (!loadSystemWideConfig()) {
        loadDefaultConfig();
      }
    }

  }

  private void loadDefaultConfig() {
    LOGGER.debug("loading default config file: {}", DEFAULT_PROPERTIES);
    this.config = new Properties();
    try {
      this.config.load(ConfigUtils.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES));
    } catch (IOException e) {
      LOGGER.error("Default config file not found! {}", e.getMessage());
    }
  }

  private boolean loadUserConfig() {
    LOGGER.debug("lookig for user defined config file: {}", USER_PROPERTIES);

    File f = new File(USER_PROPERTIES);

    if (f.exists() && f.isFile()) {
      LOGGER.debug("found user defined properties, loading.");
      try {
        this.config = new Properties();
        this.config.load(new FileInputStream(f));

        return true;

      } catch (IOException e) {
        LOGGER.warn("Could not load user defined properties file '{}', cause: {}", USER_PROPERTIES, e.getMessage());
      }
    } else {
      LOGGER.debug("User defined config file was not found.");
    }

    return false;
  }

  private boolean loadSystemWideConfig() {
    LOGGER.debug("lookig for system wide config file: {}", SYSTEM_PROPERTIES);

    File f = new File(SYSTEM_PROPERTIES);

    if (f.exists() && f.isFile()) {
      LOGGER.debug("found system wide properties, loading.");
      try {
        this.config = new Properties();
        this.config.load(new FileInputStream(f));

        return true;

      } catch (IOException e) {
        LOGGER.warn("Could not load system wide properties file '{}', cause: {}", SYSTEM_PROPERTIES, e.getMessage());
      }
    } else {
      LOGGER.debug("System wide config file was not found.");
    }

    return false;
  }

  public String getStringProperty(String key) {
    return this.config.getProperty(key, "");
  }

  public int getIntProperty(String key) {
    return Integer.parseInt(this.config.getProperty(key, "-1"));
  }
  
  public boolean getBooleanProperty(String key) {
    return Boolean.valueOf(this.config.getProperty(key, "false"));
  }

}
