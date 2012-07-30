package eu.scape_project.watch.core.scheduling.quartz.integration;

import java.util.Properties;

import eu.scape_project.watch.scheduling.quartz.QuartzScheduler;

public class SchedulerIntegrationTest {

  public static void main(String [] args) {
    SchedulerIntegrationTest test = new SchedulerIntegrationTest();
    test.testIntegration();
  }
  
  public void testIntegration() {
  
    QuartzScheduler scheduler = new QuartzScheduler();
    AdaptorPluginMock1 adaptor1 = new AdaptorPluginMock1();
    AdaptorPluginMock2 adaptor2 = new AdaptorPluginMock2();
    Properties properties = new Properties();
    properties.put("scheduler.intervalInSeconds", "5");
    AdaptorListenerMock adList = new AdaptorListenerMock();
    
    scheduler.addAdaptorListener(adList);
    
    scheduler.start(adaptor1, properties);
    properties.put("scheduler.intervalInSeconds", "1");
    
    scheduler.start(adaptor2, properties);
    
    System.out.println("hello");
    try {
      Thread.sleep(20000);
      System.out.println("hello again");
      scheduler.stop(adaptor1);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    
  }
  
  
}
