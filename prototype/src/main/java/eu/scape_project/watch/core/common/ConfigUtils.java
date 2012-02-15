package eu.scape_project.watch.core.common;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtils {
  
  public static final String KB_DATA_FOLDER_KEY = "watch.kb.data_folder";
  
  private static final String DEFAULT_PROPERTIES = "watchconfig.properties";
  
  private static final String USER_PROPERTIES = "~/.watchconfig";
  
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
    // TODO
    return false;
  }

  private boolean loadSystemWideConfig() {
    // TODO
    return false;
  }
  
  public String getStringProperty(String key) {
    return this.config.getProperty(key, "");
  }
  
  public int getIntProperty(String key) {
    return Integer.parseInt(this.config.getProperty(key, "-1"));
  }

}
