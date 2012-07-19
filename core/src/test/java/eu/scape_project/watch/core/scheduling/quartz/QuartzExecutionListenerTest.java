package eu.scape_project.watch.core.scheduling.quartz;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.scheduling.quartz.QuartzAdaptorJob;
import eu.scape_project.watch.scheduling.quartz.QuartzExecutionListener;
import eu.scape_project.watch.scheduling.quartz.QuartzScheduler;

public class QuartzExecutionListenerTest {

  @Test
  public void testRefireAdaptorWhenFailed() {
    
    QuartzExecutionListener listener = new QuartzExecutionListener();
    
    QuartzScheduler scheduler = Mockito.mock(QuartzScheduler.class);
    JobExecutionContext context = Mockito.mock(JobExecutionContext.class);
    JobExecutionException jobException = Mockito.mock(JobExecutionException.class);
    QuartzAdaptorJob adaptorJob = Mockito.mock(QuartzAdaptorJob.class);
    AdaptorPluginInterface adaptor = Mockito.mock(AdaptorPluginInterface.class);
    
    Mockito.when(adaptorJob.getAdaptorPlugin()).thenReturn(adaptor);
    Mockito.when(context.getJobInstance()).thenReturn(adaptorJob);
    
    listener.setScheduler(scheduler);
    
    listener.jobWasExecuted(context, jobException);
    Mockito.verify(scheduler,Mockito.times(1)).execute(adaptor);
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    Mockito.verify(scheduler,Mockito.times(5)).execute(adaptor);
    Mockito.verify(scheduler,Mockito.times(1)).stop(adaptor);
    
  }
}
