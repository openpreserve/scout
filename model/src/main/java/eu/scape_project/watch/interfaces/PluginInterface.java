package eu.scape_project.watch.interfaces;

import java.util.List;

import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.PluginException;


/*
 * Thanks to Luis Faria and Rui Castro for the reference implementation.
 */
/**
 * A PluginInterface interface for dynamic class loading of plugin components within the
 * watch component.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 */
public interface PluginInterface {

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

}
