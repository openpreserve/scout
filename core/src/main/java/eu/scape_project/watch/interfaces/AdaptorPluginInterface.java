package eu.scape_project.watch.interfaces;

import java.util.List;
import java.util.Map;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.plugin.ConfigParameter;
import eu.scape_project.watch.plugin.InvalidParameterException;
import eu.scape_project.watch.plugin.PluginException;
import eu.scape_project.watch.plugin.PluginInterface;
import eu.scape_project.watch.plugin.Result;

/**
 * An adaptor plugin interface that each adaptor has to implement.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public interface AdaptorPluginInterface extends PluginInterface {

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
   * @param config
   *          the configuration map. Determines what should be fetched upon
   *          execution.
   * @return the result of the execution.
   * @throws PluginException
   *           if an error occurs.
   */
  Result execute(Map<Entity, List<Property>> config) throws PluginException;

  /**
   * Fetches all information that this adaptor can obtain from the source. To be
   * used carefully as this can be a lot of data that is transfered over the
   * network. Consider using a push adaptor.
   * 
   * @return the result of the operation.
   * @throws PluginException
   *           if an error occurrs.
   */
  Result execute() throws PluginException;
}
