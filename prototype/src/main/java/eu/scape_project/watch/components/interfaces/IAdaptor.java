package eu.scape_project.watch.components.interfaces;

import java.util.List;

import eu.scape_project.watch.components.elements.Task;


/**
 * Adaptor interface
 * Adaptor know which tasks can it perform and it can accept tasks to perform 
 * @author kresimir
 *
 */
public interface IAdaptor extends Runnable {

	
	/**
	 * Check if the adaptor can perform a given task
	 * @param t
	 * @return
	 */
	public boolean checkForTask(Task t);
	
	/**
	 * Add task to the Adaptor
	 * @param t - Task to be added
	 */
	public void addTask(Task t);
	
	/**
	 * Add list of tasks to the adaptor
	 * @param tasks - Tasks to be added
	 */
	public void addTask(List<Task> t);
	
	
	
}
