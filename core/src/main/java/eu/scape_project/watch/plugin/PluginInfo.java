package eu.scape_project.watch.plugin;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.interfaces.PluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.PluginException;

/**
 * A simple class that summarizes a plugin.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@XmlRootElement(name = KBUtils.PLUGIN)
@XmlAccessorType(XmlAccessType.FIELD)
public class PluginInfo {

  /**
   * The name of the plugin.
   */
  @XmlElement
  private String name;

  /**
   * The version of the plugin.
   */
  @XmlElement
  private String version;

  /**
   * The type of the plugin.
   */
  @XmlElement
  private PluginType type;

  /**
   * The description of the plugin.
   */
  @XmlElement
  private String description;

  /**
   * The fully qualified class name of the plugin.
   */
  @XmlElement
  private String className;

  /**
   * The config parameters of this plugin.
   */
  private List<ConfigParameter> parameters;

  /**
   * Empty constructor for serialization purposes.
   */
  public PluginInfo() {
  }

  /**
   * Initializes the plugin info.
   * 
   * @param name
   *          the name.
   * @param version
   *          the version.
   * @param type
   *          the plugin type.
   * @param description
   *          the description.
   * @param className
   *          the classname.
   */
  public PluginInfo(final String name, final String version, final PluginType type, final String description,
    final String className) {
    this.name = name;
    this.version = version;
    this.type = type;
    this.description = description;
    this.className = className;
  }

  /**
   * Convinient constructor to create a plugin info from a plugin interface.
   * 
   * @param plugin
   *          The plugin interface where to get all information from.
   */
  public PluginInfo(final PluginInterface plugin) {
    this(plugin.getName(), plugin.getVersion(), plugin.getPluginType(), plugin.getDescription(), plugin.getClass()
      .getName());
    this.parameters = plugin.getParameters();
  }

  public String getName() {
    return this.name;
  }

  public String getVersion() {
    return this.version;
  }

  public PluginType getType() {
    return type;
  }

  public String getDescription() {
    return this.description;
  }

  public String getClassName() {
    return this.className;
  }

  public List<ConfigParameter> getParameters() {
    return parameters;
  }

  public void setParameters(final List<ConfigParameter> parameters) {
    this.parameters = parameters;
  }

}
