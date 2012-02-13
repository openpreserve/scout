package eu.scape_project.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.scape_project.pw.elements.Result;
import eu.scape_project.pw.elements.Task;

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
	 * all Tasks that needs to be done by the Adaptor -this should be improved - added in a separate class
	 */
	private List<Task> tasks;
	private List<Long> wrIDs;
	private List<Long> times;
	private List<Long> resetTimes;
	
	private List<Long> wrSent;
	
	private int id;
	private Thread sleeper;
	
	public AdaptorHolder() {
		isLocked = false;
		tasks = new ArrayList<Task>();
		wrIDs = new ArrayList<Long>();
		times = new ArrayList<Long>();
		resetTimes= new ArrayList<Long>();
		wrSent = new ArrayList<Long>();
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
		tasks.add(task);
		wrIDs.add(new Long(wrID));
		resetTimes.add(new Long(time));
		long tmp = (new Date()).getTime();
		times.add(new Long(tmp+1000));
		monitor.updateStartTime(id, tmp+1000);
		//System.out.println("Interrupting");
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
		wrSent.clear();
		long tempTime = System.currentTimeMillis();
		for (int i=0; i<tasks.size(); i++){
			if (Math.abs(tempTime - times.get(i))<=threshold) {
				adaptor.addTask(tasks.get(i));
				times.set(i, new Long(-1));
				wrSent.add(wrIDs.get(i));
			}
		}
	}

	public void saveResult(List<Result> results) {
		//System.out.println("Saving results");
		List<Long> tmpwr = new ArrayList<Long>();
		for (int i=0; i<times.size(); i++) {
			if (times.get(i).longValue()==-1){
				times.set(i, new Long((new Date()).getTime())+resetTimes.get(i));
				tmpwr.add(wrIDs.get(i));
			}
		}
		updateSleepTime();
		monitor.saveResult(results,tmpwr);
		isLocked=false;
	}
	
	private void updateSleepTime() {
		long min=times.get(0).longValue(); 
		for (int i=1; i<times.size(); i++) {
			if (times.get(i).longValue()<min)
				min=times.get(i).longValue();
		}
		//System.out.println("min time is "+min);
		monitor.updateStartTime(id, new Long(min));
		sleeper.interrupt();
	}
}
