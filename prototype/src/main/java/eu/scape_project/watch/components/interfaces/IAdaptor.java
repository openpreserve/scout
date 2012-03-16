package eu.scape_project.watch.components.interfaces;

import eu.scape_project.watch.core.plugin.Plugin;

/**
 * Adaptor interface Adaptor know which tasks can it perform and it can accept
 * tasks to perform
 * 
 * @author kresimir
 * 
 */
public interface IAdaptor extends Plugin {



  /**
   * Enables adaptor to read its config from a file. This will be used for constant properties 
   * like url of foreign servers, default properties that adaptor always fetches .. 
   * @param filePath
   */
  public void configureWithFile(String filePath);
  
  /**
   * Allows adaptor configuration with string. In this way adaptor will be configured 
   * dynamically. 
   * @param config
   */
  public void configureWithString(String config);
  
}
