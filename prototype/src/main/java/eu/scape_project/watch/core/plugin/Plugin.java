package eu.scape_project.watch.core.plugin;

import java.util.List;
import java.util.Map;

/*
 * Thanks to Luis Faria and Rui Castro for the reference implementation.
 */
/**
 * A Plugin interface for dynamic class loading of plugin components within the
 * watch component.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 */
public interface Plugin {

  /**
   * Initializes the plugin. This method gets called exactly once by the
   * PluginManager before the plugin gets executed. and initializing itself
   * accordingly.
   * 
   * @throws PluginException
   *           if something goes wrong during the initialization process.
   */
  void init() throws PluginException;

  /**
   * This method is called exactly once before the system shuts down or when the
   * component decides this plugin is not needed anymore. The plugin is
   * responsible for cleaning up and freeing all neeeded resources.
   * 
   * @throws PluginException
   *           if something goes wrong.
   */
  void shutdown() throws PluginException;

  /**
   * Retrieves the name of this plugin.
   * 
   * @return the name.
   */
  String getName();

  /**
   * Retrieves the version of this plugin.
   * 
   * @return the version.
   */
  String getVersion();

  /**
   * Retrieves a short human readable description of this plugin.
   * 
   * @return the description.
   */
  String getDescription();

  /**
   * Retrieves the type of the plugin.
   * 
   * @return the plugin type.
   */
  PluginType getPluginType();

  /**
   * Retrieves a list with {@link ConfigParameter} objects needed/supported by
   * this plugin. Note the not all config parameters have to be requried (
   * {@link ConfigParameter#isRequired()} )
   * 
   * @return the list with the parameters.
   */
  List<ConfigParameter> getParameters();

  /**
   * Returns a pre-polulated map with the configuration of this plugin. The keys
   * are the config parameters and the values are the config values. The plugin
   * can provide default values, but is not expected to.
   * 
   * @return the config of the plugin.
   */
  Map<String, String> getParameterValues();

  /**
   * Sets the config parameters.
   * 
   * @param values
   *          sets the values.
   * @throws InvalidParameterException
   *           if some required parameters are not provided, or other problems
   *           occur.
   */
  void setParameterValues(Map<String, String> values) throws InvalidParameterException;

  /**
   * Executes this plugin and returns a specific {@link Result} implementation.
   * The execution has to be done according to the current parameter setting.
   * 
   * @return the result of the execution.
   * @throws PluginException
   *           if an error occurs.
   */
  Result execute() throws PluginException;
}
