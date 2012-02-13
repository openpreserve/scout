package eu.scape_project.components;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.components.interfaces.IMonitor;
import eu.scape_project.pw.elements.Question;
import eu.scape_project.pw.elements.WatchRequest;

public class CentralMonitor extends Thread{

	private List<IMonitor> monitors;
	
	public CentralMonitor() {
		monitors = new ArrayList<IMonitor>();
		//System.out.println("Central service started");
	}
	
	public void registerMonitor(IMonitor monitor){
		monitor.registerCentralMonitor(this);
		monitors.add(monitor);
	}
	
	public void addWatchRequest(WatchRequest wr) {
		List<Question> tmp = wr.getQuestions();
		for (Question q: tmp){
			addWatchQuestion(q,wr);
		}
	}
	
	public synchronized void notifyWatchRequests(List<Long> wrIds){
		System.out.println("Notifying " +wrIds);
	}
	
	public void run() {
		for (int i=0; i<monitors.size(); i++) {
			Thread t = monitors.get(i).startMonitoring();
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void addWatchQuestion(Question q,WatchRequest wr){
		for (int i=0; i<monitors.size(); i++) {
			if (monitors.get(i).checkForEntityType(q.getEntityType())) {
				//System.out.println("Adding watch question");
				monitors.get(i).addQuestion(q,wr.getid(),q.getTime());
				return; //only one monitor will answer question 
			}
		}
	}
	
}
