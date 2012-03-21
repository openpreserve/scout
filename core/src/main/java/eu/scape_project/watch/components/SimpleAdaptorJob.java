package eu.scape_project.watch.components;

import java.util.Properties;

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
      .usingJobData("adaptorVersion", properties.getProperty("adaptor.version")).build();

    trigger = TriggerBuilder
      .newTrigger()
      .withIdentity("trigger_" + properties.getProperty("adaptor.name"), properties.getProperty("adaptor.group"))
      .startNow()
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInSeconds(Integer.parseInt(properties.getProperty("adaptor.trigger.interval"))).repeatForever()).build();
  }

}
