package eu.scape_project.watch.components;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.elements.WatchRequest;
import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IMonitor;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.Question;

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
    Question q1 = new Question(" ",et1,e1,p1,10000);
    WatchRequest wr1 = new WatchRequest();
    wr1.addQuestion(q1);
    EntityType et12 = new EntityType();
    Entity e12 = new Entity();
    Property p12 = new Property();
    et12.setName("format");
    e12.setName("pdf");
    p12.setName("number_of_browser");
    Question q12 = new Question(" ",et12,e12,p12,20000);
    wr1.addQuestion(q12);
    cm.addWatchRequest(wr1);
    
    
    
    
    //watch request 2
    EntityType et2 = new EntityType();
    Entity e2 = new Entity();
    Property p2 = new Property();
    et2.setName("format");
    e2.setName("tiff");
    p2.setName("tool_support");
    Question q2 = new Question(" ",et2,e2,p2,100);
    WatchRequest wr2 = new WatchRequest();
    wr2.addQuestion(q2);
    cm.addWatchRequest(wr2);
    
    
    try {
		t.join();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
  
