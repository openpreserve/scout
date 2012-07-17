package eu.scape_project.watch.interfaces;

import java.util.List;
import java.util.Map;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * An adaptor plugin interface that each source adaptor has to implement. It
 * provides the basic agreement between Scout's core and the adaptors. In
 * general, the adaptors act like iterators over their own source. It is up to
 * the specific implementation to decide, whether the values will fetched at
 * once and cached or fetched on demand.
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
   * To be removed in version 0.0.4. Executes this plugin and returns a specific
   * {@link ResultInterface} implementation. The execution has to be done
   * according to the current parameter setting.
   * 
   * @param config
   *          the configuration map. Determines what should be fetched upon
   *          execution.
   * @return the result of the execution.
   * @throws PluginException
   *           if an error occurs.
   */
  @Deprecated
  ResultInterface execute(Map<Entity, List<Property>> config) throws PluginException;

  /**
   * To be removed in version 0.0.4. Fetches all information that this adaptor
   * can obtain from the source. To be used carefully as this can be a lot of
   * data that is transfered over the network. Consider using a push adaptor.
   * 
   * @return the result of the operation.
   * @throws PluginException
   *           if an error occurs.
   */
  @Deprecated
  ResultInterface execute() throws PluginException;

  /**
   * Checks with the source whether there is more info to be fetched. If there
   * is the method returns true, false otherwise. Note that the method might
   * call the source and even fetch and cache some results.
   * 
   * @return true if there are more results to be fetched, false otherwise.
   * @throws PluginException
   *           if an error occurs.
   */
  boolean hasNext() throws PluginException;

  /**
   * This method retrieves the next measurement result ( {@link PropertyValue},
   * {@link Property}, {@link Entity} ). This method might return null. It is in
   * the clients responsibility of this class to make sure that there is a next
   * result by calling the {@link AdaptorPluginInterface#hasNext()} method.
   * 
   * @return the result.
   * @see ResultInterface
   */
  ResultInterface next();


}
