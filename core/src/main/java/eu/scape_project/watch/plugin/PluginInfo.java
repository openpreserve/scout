package eu.scape_project.watch.plugin;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.KBUtils;

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
  private String desc;

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
   * @param n
   *          the name.
   * @param v
   *          the version.
   * @param type
   *          the plugin type.
   * @param d
   *          the description.
   * @param c
   *          the classname.
   */
  public PluginInfo(final String n, final String v, final PluginType type, final String d, final String c) {
    this.name = n;
    this.version = v;
    this.type = type;
    this.desc = d;
    this.className = c;
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
    return this.desc;
  }

  public String getClassName() {
    return this.className;
  }

  public List<ConfigParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<ConfigParameter> parameters) {
    this.parameters = parameters;
  }

}
