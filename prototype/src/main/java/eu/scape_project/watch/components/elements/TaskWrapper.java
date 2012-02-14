package eu.scape_project.watch.components.elements;

import java.util.Date;


/**
 * Class which wrapps Task class with additional information like 
 * 	- how often does it need to be executed (adaptor fetches data)
 *  - when is the next scheduled time for fetching data
 *  - watch request id that is interested in that task 
 * @author kresimir
 *
 */
public class TaskWrapper {

	private Task task;
	private long nextTime;
	private long period;
	private long wrid;
	
	public TaskWrapper() {}
	
	public TaskWrapper(Task t, long p, long w){
		task = t;
		period = p;
		wrid = w;
		nextTime = (new Date()).getTime()+period;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public long getNextTime() {
		return nextTime;
	}

	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getWrid() {
		return wrid;
	}

	public void setWrid(long wrid) {
		this.wrid = wrid;
	}
	
	public void resetTime() {
		nextTime = (new Date()).getTime()+period;
	}
}
