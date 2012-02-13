package eu.scape_project.watch.components.elements;


import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.scape_project.watch.components.AdaptorHolder;
import eu.scape_project.watch.components.Monitor;


public class Executor implements Runnable {

	private ExecutorService pool;
	private Monitor monitor;
	
	public Executor() {
		pool = Executors.newFixedThreadPool(4);
	}
	
	public Executor(Monitor m){
		monitor = m;
		pool = Executors.newFixedThreadPool(4);
	}
	
	@Override
	public void run() {
		List<AdaptorHolder> adaptorsHolders = monitor.getAdaptorHolder();
		List<Long> sleepTime = monitor.getSleepTime();
		//System.out.println(sleepTime);
		long currTime =(new Date()).getTime();
		//System.out.println("Starting adaptor execution");
		for (int i=0; i< adaptorsHolders.size(); i++) {
			if (sleepTime.get(i)==-1)
				continue;
			if (Math.abs(sleepTime.get(i)-currTime)<100 || sleepTime.get(i)<currTime) {
				if (!adaptorsHolders.get(i).isLocked()) {
					//System.out.println("Adaptor started");
					adaptorsHolders.get(i).lock();
					sleepTime.set(i, new Long(-1));
					pool.submit(adaptorsHolders.get(i).getAdaptor());
				}
			}
		}
		
	}
}
