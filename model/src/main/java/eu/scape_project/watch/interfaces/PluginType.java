package eu.scape_project.watch.interfaces;

/**
 * Defines the type of the plugin.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public enum PluginType {

  /**
   * The adaptor plugin type.
   */
  ADAPTOR,

  /**
   * The Notification plugin type.
   */
  NOTIFICATION;

  /**
   * Checks if the target is equal to the examine type and returns true if they
   * are and false otherwise. In the special case that the target is null the
   * method always returns true. This convention can be used to match all types
   * of plugins when searching.
   * 
   * @param target
   *          the type of plugins you are looking for
   * @param examine
   *          the type of plugin you have at hand
   * @return whether or not the types match.
   */
  public static boolean match(PluginType target, PluginType examine) {
    if (target == null) {
      return true;
    }

    return target == examine;
  }
}
