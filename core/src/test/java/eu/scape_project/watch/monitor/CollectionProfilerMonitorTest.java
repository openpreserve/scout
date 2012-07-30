package eu.scape_project.watch.monitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;

public class CollectionProfilerMonitorTest {

  
//  @Test 
//  public void testAddRemoveAsyncRequest() {
//    
//    CollectionProfilerMonitor cpm = new CollectionProfilerMonitor();
//    EntityType et = new EntityType("CollectionProfile","");
//    Property prop = new Property(et, "size", "");
//    Entity ent = new Entity(et, "coll-0-test");
//    Question question1 = new Question("", RequestTarget.PROPERTY_VALUE,
//      Arrays.asList(et), Arrays.asList(prop), Arrays.asList(ent), 60);
//    Map<String, String> not1config = new HashMap<String, String>();
//    not1config.put("to", "test");
//    not1config.put("subject", "test");
//    Notification notification1 = new Notification("log", not1config);
//    Trigger trigger1 = new Trigger(question1, Arrays.asList(notification1), null);
//    
//    AsyncRequest asRe = new AsyncRequest();
//    asRe.addTrigger(trigger1);
//    
//    cpm.addWatchRequest(asRe);
//    
//    Assert.assertTrue(cpm.getAsyncRequestUUIDS().contains(asRe.getId()));
//    
//    cpm.addWatchRequest(asRe);
//    
//    Assert.assertTrue(cpm.getAsyncRequestUUIDS().size()==1);
//    
//    EntityType et2 = new EntityType("Format","");
//    Question question2 = new Question("", RequestTarget.PROPERTY_VALUE,
//      Arrays.asList(et2), Arrays.asList(prop), Arrays.asList(ent), 60);
//    Trigger trigger2 = new Trigger(question2, Arrays.asList(notification1), null);
//    
//    AsyncRequest asRe2 = new AsyncRequest();
//    asRe2.addTrigger(trigger2);
//    
//    cpm.addWatchRequest(asRe2);
//    
//    Assert.assertFalse(cpm.getAsyncRequestUUIDS().contains(asRe2.getId()));
//    
//    cpm.removeWatchRequest(asRe2);
//    Assert.assertTrue(cpm.getAsyncRequestUUIDS().size()==1);
//    
//    cpm.removeWatchRequest(asRe);
//    Assert.assertTrue(cpm.getAsyncRequestUUIDS().size()==0);
//    
//    
//  }
}
