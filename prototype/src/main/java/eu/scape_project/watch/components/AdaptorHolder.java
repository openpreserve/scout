package eu.scape_project.watch.components;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.components.elements.TaskWrapper;

/**
 * This class wraps the Adaptor class so that logic for comunicating between the Monitor and the Adaptor is removed from the Adaptor  
 * @author kresimir
 *
 */
public class AdaptorHolder {
	
	/**
	 * adaptor 
	 */
	private Adaptor adaptor; 
	
	/**
	 * is Adaptor running (it is not allowed to start same adaptor twice in parallel)
	 */
	private boolean isLocked;
	
	/***
	 * Monitor to which AdaptorHolder belongs
	 */
	private Monitor monitor;
	
	/**
	 * threshold for activating adaptor (this should be fetched from the adaptor 
	 */
	private long threshold = 1000;
	
	/**
	 * all Tasks that need to be done by the Adaptor 
	 */
	private List<TaskWrapper> tasks;
	
	/**
	 * AdaptorHolder id 
	 */
	private int id;
	
	
	/**
	 * next scheduled time to activate adaptor 
	 */
	private long nextTime;
	
	/**
	 * thread that activates adaptor 
	 */
	private Thread sleeper;
	
	
	
	public AdaptorHolder() {
		isLocked = false;
		tasks = new ArrayList<TaskWrapper>();
		nextTime = Long.MAX_VALUE;
	}
	
	public AdaptorHolder(int id, Monitor monitor, Adaptor adaptor, Thread sleeper){
		this();
		this.id=id;
		this.monitor=monitor;
		this.adaptor=adaptor;
		this.sleeper=sleeper;
		this.adaptor.addAdaptorHolder(this);
	}
	
	
	public void registerAdapter(Adaptor adaptor) {
		this.adaptor = adaptor;
		this.adaptor.addAdaptorHolder(this);
	}

	public void registerMonitor(Monitor monitor) {
		this.monitor=monitor;
	}
	
	public boolean checkForTask(Task task) {
		return adaptor.checkForTask(task);
	}

	public void addTask(Task task, long wrID, long time) {
		TaskWrapper tmp = new TaskWrapper(task,time,wrID);
		tasks.add(tmp);
		nextTime = (tmp.getNextTime()<nextTime) ? tmp.getNextTime() : nextTime; 
		sleeper.interrupt();
	}

	/**
	 * prepare adaptor for execution and returns it
	 * @return
	 */
	public Adaptor getAdaptor() {
		prepareAdaptor();
		return adaptor;
	}

	public void lock() {
		isLocked=true;
		
	}

	public void unlock() {
		isLocked=false;
		
	}

	public boolean isLocked() {
		return isLocked;
	}

	public long getNextTime()  {
		return nextTime;
	}
	
	private void prepareAdaptor() {
		long tempTime = System.currentTimeMillis();
		for (int i=0; i<tasks.size(); i++){
			if (Math.abs(tempTime - tasks.get(i).getNextTime())<=threshold) {
				adaptor.addTask(tasks.get(i).getTask());
				tasks.get(i).setNextTime(Long.MAX_VALUE);
			}
		}
		nextTime = Long.MAX_VALUE; // adaptor is running 
	}

	public void saveResult(List<Result> results) {
		List<Long> tmpwr = new ArrayList<Long>();
		for (int i=0; i<tasks.size(); i++) {
			if (tasks.get(i).getNextTime()==Long.MAX_VALUE){
				tasks.get(i).resetTime();
				tmpwr.add(tasks.get(i).getWrid());
			}
			nextTime = (tasks.get(i).getNextTime()<nextTime) ? tasks.get(i).getNextTime() : nextTime;
		}
		monitor.saveResult(results,tmpwr);
		isLocked=false;
		sleeper.interrupt();
	}

}
