package eu.scape_project.watch.scheduling;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAdaptorJob extends AdaptorJob {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleAdaptorJob.class);

  private JobDetail jobDetail;
  private Trigger trigger;

  @Override
  public JobDetail getJobDetail() {
    return jobDetail;
  }

  @Override
  public Trigger getTrigger() {
    return trigger;
  }

  @Override
  protected void initialize() {
    Properties properties = this.getProperties();
    
    jobDetail = JobBuilder.newJob(SimpleAdaptorJob.class)
      .withIdentity(properties.getProperty("adaptor.name"), properties.getProperty("adaptor.group"))
      .usingJobData("adaptorClassName", properties.getProperty("adaptor.class"))
      .usingJobData("adaptorVersion", properties.getProperty("adaptor.version"))
      .usingJobData("adaptorProperties", this.adaptorPropertiesToString(properties)).build();

    
    trigger = TriggerBuilder
      .newTrigger()
      .withIdentity("trigger_" + properties.getProperty("adaptor.name"), properties.getProperty("adaptor.group"))
      .startNow()
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInSeconds(Integer.parseInt(properties.getProperty("adaptor.trigger.interval"))).repeatForever()).build();
  }

  private String adaptorPropertiesToString(Properties properties) {
    StringBuffer tProperties = new StringBuffer();
    
    String prefix = properties.getProperty("adaptor.config.prefix");
    Set<String> keys = properties.stringPropertyNames();
    LOG.trace("loaded properties size {}", keys.size());
    Iterator<String> it = keys.iterator();
    while (it.hasNext()){
      String key = it.next();
      if (key.startsWith(prefix)){
        String value = properties.getProperty(key);
        tProperties.append(key+"="+value+"\n");
      }
    }
    LOG.trace("properties for adaptor "+ tProperties);
    return tProperties.toString();
  }
}
