package eu.scape_project.watch.components.interfaces;

import eu.scape_project.watch.components.elements.Result;

/**
 * Adaptor interface Adaptor know which tasks can it perform and it can accept
 * tasks to perform
 * 
 * @author kresimir
 * 
 */
  
public interface IAdaptor extends Job {

  public void configure(/*add argument*/);
  
}
