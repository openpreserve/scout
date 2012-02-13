package eu.scape_project.components.interfaces;

import java.util.List;

import eu.scape_project.components.CentralMonitor;
import eu.scape_project.pw.elements.Question;
import eu.scape_project.pw.elements.Result;
import eu.scape_project.watch.core.model.EntityType;

public interface IMonitor {


	/**
	 * Registering a new Adaptor 
	 * @param adaptor
	 */
	public void registerAdaptor(IAdaptor adaptor);
	
	/**
	 * Check if a Monitor can fetch data (through) adaptors about Entities with a specified EntityType
	 * @param t - specified EntityType
	 * @return tru if it can fetch data false otherwise
	 */
	public boolean checkForEntityType(EntityType t);
	
	/**
	 * Add a question to the Monitor 
	 * @param result
	 */
	public void addQuestion(Question q, long wrId, long time);

	
	/**
	 * Saves recieved results to a database and notifies (if needed) CentralMonitor  
	 * @param result
	 */
	public void saveResult(Result result);
	
	/**
	 * 
	 * @param results
	 */
	public void saveResult(List<Result> results, List<Long> wrIds); 
	
	
	
	/**
	 * 
	 * @param cm
	 */
	public void registerCentralMonitor(CentralMonitor cm);
	
	/**
	 * 
	 * @return
	 */
	public Thread startMonitoring();
	
}
