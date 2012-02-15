package eu.scape_project.watch.components;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.Question;

public class MonitorTest {

	@Test 
	public void testMonitor() {
		Monitor tMonitor = new Monitor();
		Assert.assertTrue(tMonitor.getAdaptorHolder()!=null);
	}
	@Test 
	public void testRegisterAdaptor() {
		Monitor tMonitor = new Monitor();
		tMonitor.registerAdaptor(new DummyAdaptor());
		Assert.assertTrue(tMonitor.getAdaptorHolder().size()==1);
		tMonitor.registerAdaptor(new DummyAdaptor());
		Assert.assertTrue(tMonitor.getAdaptorHolder().size()==2);
	}
	
	@Test 
	public void testSleepTime() {
		Monitor tMonitor = new Monitor();
		tMonitor.registerAdaptor(new DummyAdaptor());
		tMonitor.registerAdaptor(new DummyAdaptor2());
		Assert.assertTrue(tMonitor.getAdaptorHolder().size()==2);
		
		EntityType et1 = new EntityType();
	    Entity e1 = new Entity();
	    Property p1 = new Property();
	    et1.setName("format");
	    e1.setName("pdf");
	    p1.setName("tool_support");
	    Question q1 = new Question(" ",et1,e1,p1,10000);
	    
	    //tMonitor.addQuestion(q1, 0, 10000);
	    Date curr = new Date();
	    //Assert.assertTrue(tMonitor.minSleepTime()<=10000);
	}
}
