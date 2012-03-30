package eu.scape_project.watch.scheduling;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

public class SimpleAdaptorJobTest {

  private String testProperties = "adaptor.name=test_adaptor\n" +
  		"adaptor.group=test_group\n" +
  		"adaptor.class=test_class\n" +
  		"adaptor.trigger.interval=10\n" +
  		"adaptor.version=test_version\n" +
  		"adaptor.config.prefix=test\n" +
  		"test.parameter1=scout\n" +
  		"test.parameter2=watch";
  
  private Properties properties; 
  
  @Before 
  public void init() {
    properties = new Properties();
    try {
      properties.load(new ByteArrayInputStream(testProperties.getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  @Test
  public void testInitialize() {
    SimpleAdaptorJob sJob = new SimpleAdaptorJob(); 
    Assert.assertTrue(sJob.getJobDetail()==null);
    
    sJob.initialize(properties);
    
    JobDetail jobDetail = sJob.getJobDetail();
    Assert.assertTrue(jobDetail.getKey().getName().equals("test_adaptor") 
                                      && jobDetail.getKey().getGroup().equals("test_group"));
    JobDataMap jobMap = sJob.getJobDetail().getJobDataMap();
    Assert.assertTrue(jobMap.getString("adaptorClassName").equals("test_class"));
    Assert.assertTrue(jobMap.getString("adaptorProperties").contains("test.parameter1=scout\n") && 
      jobMap.getString("adaptorProperties").contains("test.parameter2=watch"));
  }
}
