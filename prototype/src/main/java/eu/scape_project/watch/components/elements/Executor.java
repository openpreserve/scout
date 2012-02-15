package eu.scape_project.watch.components.elements;


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
		long currTime =System.currentTimeMillis();
		for (int i=0; i< adaptorsHolders.size(); i++) {
			if (adaptorsHolders.get(i).getNextTime()==Long.MAX_VALUE)
				continue;
			if (Math.abs(adaptorsHolders.get(i).getNextTime()-currTime)<100 || adaptorsHolders.get(i).getNextTime()<currTime) {
				if (!adaptorsHolders.get(i).isLocked()) {
					adaptorsHolders.get(i).lock();
					pool.submit(adaptorsHolders.get(i).getAdaptor());
				}
			}
		}
		
	}
}
