package eu.scape_project.watch.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eu.scape_project.watch.components.elements.Executor;
import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IMonitor;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Question;

public class Monitor extends Thread implements IMonitor{
	
	/**
	 * entity type that is supported with this monitor
	 */
	private EntityType entityType;
	
	/**
	 * adaptor holders 
	 */
	private List<AdaptorHolder> adaptorsHolders;
	
	/**
	 * date when the adaptor is executed
	 */
	//private List<Long> sleepTime;
	
	private Thread sleeper;
	private CentralMonitor center;

	
	public Monitor() {
		adaptorsHolders = new ArrayList<AdaptorHolder>();
		//sleepTime = new ArrayList<Long>();
		sleeper = new Thread(this);
	}

	public Monitor(EntityType t){
		this();
		entityType=t;
	}

	
	public List<AdaptorHolder> getAdaptorHolder() {
		return adaptorsHolders;
	}
	
	public void setAdaptorHolder(List<AdaptorHolder> ah){
		adaptorsHolders=ah;
	}
	
	/*public List<Long> getSleepTime() {
		return sleepTime;
	}*/

	/*public void updateStartTime(int id, Long time) {
		sleepTime.set(id, time);
	}*/

	private long minSleepTime(){
		Calendar cal = Calendar.getInstance();
		long currTime = System.currentTimeMillis();
		cal.set(2020, 12, 25);
		long minSt = cal.getTimeInMillis() - currTime; 
		for (int i=0; i<adaptorsHolders.size(); i++) {
			if (adaptorsHolders.get(i).getNextTime()==Long.MAX_VALUE)
				continue;
			if (adaptorsHolders.get(i).getNextTime()-currTime<minSt)
				minSt=adaptorsHolders.get(i).getNextTime()-currTime;
		}
		return minSt;
	}

	private void activateAdaptors() {
		Thread t = new Thread(new Executor(this));
		t.start();
	}
	
	@Override
	public boolean checkForEntityType(EntityType t) {
		return entityType.equals(t);
	}

	@Override
	public void addQuestion(Question q,String wrId, long time) {
		Task tempTask = new Task(q.getEntity(),q.getProperty());
		for (int i=0; i<adaptorsHolders.size(); i++){
			if (adaptorsHolders.get(i).checkForTask(tempTask)) {
				adaptorsHolders.get(i).addTask(tempTask, wrId, time);
				return;
			}
		}
		
	}


	@Override
	public void saveResult(Result result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void saveResult(List<Result> results, List<String> wrIds) {
		//save results to a database
		center.notifyWatchRequests(wrIds);
		
	}

	@Override
	public void registerCentralMonitor(CentralMonitor cm) {
		center = cm;
	}

	
	
	
	@Override
	public Thread startMonitoring()  {
		sleeper.start();
		return sleeper;
	}
	
	@Override
	public void registerAdaptor(IAdaptor adaptor){
		AdaptorHolder temp = new AdaptorHolder(adaptorsHolders.size(),this,(Adaptor)adaptor,sleeper); //this casting is not a nice solution
		adaptorsHolders.add(temp);
		//sleepTime.add(new Long(-1));
		//System.out.println("Adaptor added");
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
				//activateAdaptors();
				//continue;
			}
			//System.out.println("Activating adaptor");
			activateAdaptors();
		}
	}
	
	
	
	
}
