package eu.scape_project.watch.components;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.components.interfaces.IAdaptor;


/**
 * Abstract implementation of common functionality for Adaptors. Other Adaptors are 
 * supposed to extend this class but not necessary. 
 * @author kresimir
 *
 */
public abstract class Adaptor implements IAdaptor{

	
	
	protected List<Task> tasks; 
	protected List<Result> results;
	
	private AdaptorHolder callback;

	public Adaptor() {
		tasks = new ArrayList<Task>();
		results = new ArrayList<Result>();
	}

	@Override
	public void addTask(Task t) {
		tasks.add(t);
	}

	@Override
	public void addTask(List<Task> t) {
		tasks.addAll(t);
	}
		
	
	public void addAdaptorHolder(AdaptorHolder a) {
		callback = a;
		//System.out.println("Added adaptor holder");
	}
	
	@Override
	public void run() {
		fetchData();
		//System.out.println("Data fetched");
		tasks.clear();
		callback.saveResult(results);
		results.clear();
	}
	
	
	/**
	 * method for fetching the data 
	 */
	protected abstract void fetchData();
	
	
}
