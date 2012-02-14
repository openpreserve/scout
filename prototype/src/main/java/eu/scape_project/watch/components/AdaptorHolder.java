package eu.scape_project.watch.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.components.elements.TaskWrapper;

/**
 * class that holds Adaptor and manages it when it needs to fetch the data  
 * @author kresimir
 *
 */
public class AdaptorHolder {
	
	/**
	 * adaptor 
	 */
	private Adaptor adaptor; 
	
	/**
	 * is Adaptor running ( it is not allowed to start same adaptor twice in parallel)
	 */
	private boolean isLocked;
	
	/***
	 * Monitor to which AdaptorHolder belongs
	 */
	private Monitor monitor;
	
	
	private long threshold = 1000;
	
	/**
	 * all Tasks that need to be done by the Adaptor 
	 */
	private List<TaskWrapper> tasks;
	
	private int id;
	private Thread sleeper;
	
	public AdaptorHolder() {
		isLocked = false;
		tasks = new ArrayList<TaskWrapper>();
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
		monitor.updateStartTime(id, tmp.getNextTime());
		sleeper.interrupt();
	}

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

	
	private void prepareAdaptor() {
		long tempTime = System.currentTimeMillis();
		for (int i=0; i<tasks.size(); i++){
			if (Math.abs(tempTime - tasks.get(i).getNextTime())<=threshold) {
				adaptor.addTask(tasks.get(i).getTask());
				tasks.get(i).setNextTime(-1);
			}
		}
	}

	public void saveResult(List<Result> results) {
		List<Long> tmpwr = new ArrayList<Long>();
		for (int i=0; i<tasks.size(); i++) {
			if (tasks.get(i).getNextTime()==-1){
				tasks.get(i).resetTime();
				tmpwr.add(tasks.get(i).getWrid());
			}
		}
		updateSleepTime();
		monitor.saveResult(results,tmpwr);
		isLocked=false;
	}
	
	private void updateSleepTime() {
		long min = tasks.get(0).getNextTime();
		for (int i=1; i<tasks.size(); i++) {
			if (tasks.get(i).getNextTime()<min)
				min=tasks.get(i).getNextTime();
		}
		monitor.updateStartTime(id, new Long(min));
		sleeper.interrupt();
	}
}
