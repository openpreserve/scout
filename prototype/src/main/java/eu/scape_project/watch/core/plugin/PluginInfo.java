package eu.scape_project.watch.core.plugin;

/*
 * Eventually we will need the config parameters here as well.
 */
/**
 * A simple class that summarizes a plugin.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PluginInfo {

  /**
   * The name of the plugin.
   */
  private String name;

  /**
   * The version of the plugin.
   */
  private String version;

  /**
   * The description of the plugin.
   */
  private String desc;

  /**
   * The fully qualified class name of the plugin.
   */
  private String className;

  /**
   * Initializes the plugin info.
   * 
   * @param n
   *          the name.
   * @param v
   *          the version.
   * @param d
   *          the description.
   * @param c
   *          the classname.
   */
  public PluginInfo(final String n, final String v, final String d, final String c) {
    this.name = n;
    this.version = v;
    this.desc = d;
    this.className = c;
  }

  public String getName() {
    return this.name;
  }

  public String getVersion() {
    return this.version;
  }

  public String getDescription() {
    return this.desc;
  }

  public String getClassName() {
    return this.className;
  }
}
