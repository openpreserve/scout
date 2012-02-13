package eu.scape_project.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eu.scape_project.components.interfaces.IAdaptor;
import eu.scape_project.components.interfaces.IMonitor;
import eu.scape_project.pw.elements.Executor;
import eu.scape_project.pw.elements.Question;
import eu.scape_project.pw.elements.Result;
import eu.scape_project.pw.elements.Task;
import eu.scape_project.watch.core.model.EntityType;

public class Monitor extends Thread implements IMonitor{
	
	private EntityType entityType;
	
	@Override
	public boolean checkForEntityType(EntityType t) {
		return entityType.equals(t);
	}

	@Override
	public void addQuestion(Question q,long wrId, long time) {
		Task tempTask = new Task(q.getEntity(),q.getProperty());
		for (int i=0; i<adaptorsHolders.size(); i++){
			if (adaptorsHolders.get(i).checkForTask(tempTask)) {
				adaptorsHolders.get(i).addTask(tempTask, wrId, time);
				return;
			}
		}
		
	}

	private List<AdaptorHolder> adaptorsHolders;
	
	/**
	 * date when the adaptor is executed
	 */
	private List<Long> sleepTime;
	
	private Thread sleeper;
	private CentralMonitor center;
	

	

	@Override
	public void saveResult(Result result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void saveResult(List<Result> results, List<Long> wrIds) {
		System.out.println("Saving results to a database " +results);
		center.notifyWatchRequests(wrIds);
		
	}

	@Override
	public void registerCentralMonitor(CentralMonitor cm) {
		center = cm;
	}

	public Monitor() {
		adaptorsHolders = new ArrayList<AdaptorHolder>();
		sleepTime = new ArrayList<Long>();
		System.out.println("Initial time to sleep is "+sleepTime);
		sleeper = new Thread(this);
	}
	
	private long minSleepTime(){
		Calendar cal = Calendar.getInstance();
		Date curr = new Date();
		long currTime = curr.getTime();
		cal.set(2020, 12, 25);
		long minSt = cal.getTimeInMillis() - currTime; 
		for (int i=0; i<sleepTime.size(); i++) {
			if (sleepTime.get(i).longValue()==-1)
				continue;
			if (sleepTime.get(i).longValue()-currTime<minSt)
				minSt=sleepTime.get(i).longValue()-currTime;
		}
		return minSt;
	}
	
	public Monitor(EntityType t){
		this();
		entityType=t;
		//System.out.println("Monitor with entity type "+ t.getName()+" started");
	}
	
	@Override
	public Thread startMonitoring()  {
		sleeper.start();
		return sleeper;
	}
	
	public void registerAdaptor(IAdaptor adaptor){
		AdaptorHolder temp = new AdaptorHolder(adaptorsHolders.size(),this,(Adaptor)adaptor,sleeper); //this casting is not a nice solution
		adaptorsHolders.add(temp);
		sleepTime.add(new Long(-1));
		//System.out.println("Adaptor added");
	}

	public void updateStartTime(int id, Long time) {
		//System.out.println("Updating time");
		sleepTime.set(id, time);
		//System.out.println("SleepTime set to "+sleepTime);
	}
	
	@Override
	public void run() { 
		while (true) {
			try {
				long tmp = minSleepTime();
				if (tmp<=0)
					tmp = 10;
				//System.out.println("Going to sleep "+tmp);
				Thread.sleep(tmp);
			} catch (InterruptedException e) {
				//System.out.println("Waking up");
				activateAdaptors();
				continue;
			}
			activateAdaptors();
		}
	}
	
	private void activateAdaptors() {
		Thread t = new Thread(new Executor(this));
		t.start();
	}
	
	public List<AdaptorHolder> getAdaptorHolder() {
		return adaptorsHolders;
	}
	
	public List<Long> getSleepTime() {
		return sleepTime;
	}
	
}
