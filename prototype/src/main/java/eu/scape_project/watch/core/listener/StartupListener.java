package eu.scape_project.watch.core.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.SimpleAdaptorJob;
import eu.scape_project.watch.components.listeners.CollectionProfilerListener;
import eu.scape_project.watch.core.CoreScheduler;

/**
 * 
 * @author kresimir
 * 
 */
public class StartupListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    CoreScheduler cs = CoreScheduler.getCoreScheduler();
    cs.shutdown();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    LOG.info("Hello from Startup");
    CoreScheduler cs = CoreScheduler.getCoreScheduler();
    JobDetail job = JobBuilder.newJob(SimpleAdaptorJob.class).withIdentity("c3po", "CollectionProfileAdaptors")
      .usingJobData("adaptor", "eu.scape_project.watch.components.adaptors.c3po.C3POAdaptor").build();

    Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "CollectionProfileAdaptors").startNow()
      .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();

    cs.adddGroupJobListener(new CollectionProfilerListener(), "CollectionProfileAdaptors");

    cs.scheduleJob(job, trigger);
    cs.start();
  }

}
