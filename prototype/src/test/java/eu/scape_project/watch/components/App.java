package eu.scape_project.watch.components;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IMonitor;
import eu.scape_project.watch.core.model.AsyncRequest;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.Question;
import eu.scape_project.watch.core.model.Trigger;

/**
 * Hello world!
 * 
 */
public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    /*System.out.println("Happy Coding!");
    App.LOG.debug("Happy Coding!");
    */
	
	long tmp = (new Date()).getTime();
	System.out.println("Current time "+tmp);  
    CentralMonitor cm = new CentralMonitor();
    EntityType en = new EntityType();
    en.setName("format");
    IMonitor mon = new Monitor(en);
    IAdaptor add = new DummyAdaptor();
    mon.registerAdaptor(add);
    
    IAdaptor add2 = new DummyAdaptor2();
    mon.registerAdaptor(add2);
    
    cm.registerMonitor(mon);
    Thread t = new Thread(cm);
    t.start();
   
    //watch request 1 
    EntityType et1 = new EntityType();
    Entity e1 = new Entity();
    Property p1 = new Property();
    et1.setName("format");
    e1.setName("pdf");
    p1.setName("tool_support");
    Question q1 = new Question(" ",et1,e1,p1,100);
    AsyncRequest wr1 = new AsyncRequest();
    Trigger t1 = new Trigger();
    t1.setQuestion(q1);
    wr1.addTrigger(t1);
    EntityType et12 = new EntityType();
    Entity e12 = new Entity();
    Property p12 = new Property();
    et12.setName("format");
    e12.setName("pdf");
    p12.setName("number_of_browser");
    Question q12 = new Question(" ",et12,e12,p12,1500);
    Trigger t12 = new Trigger();
    t12.setQuestion(q12);
    
    wr1.addTrigger(t12);
    cm.addWatchRequest(wr1);
    System.out.println("Request UUID: "+wr1.getId());
    
    
    
    //watch request 2
    EntityType et2 = new EntityType();
    Entity e2 = new Entity();
    Property p2 = new Property();
    et2.setName("format");
    e2.setName("tiff");
    p2.setName("tool_support");
    Question q2 = new Question(" ",et2,e2,p2,1000);
    
    AsyncRequest wr2 = new AsyncRequest();
    Trigger t2 = new Trigger();
    t2.setQuestion(q2);
    wr2.addTrigger(t2);
    cm.addWatchRequest(wr2);
    System.out.println("Request UUID: "+wr2.getId());
    
    
    try {
		t.join();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
  
