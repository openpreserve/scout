package eu.scape_project.watch.core.scheduling.quartz;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.scheduling.quartz.QuartzAdaptorJob;
import eu.scape_project.watch.scheduling.quartz.QuartzExecutionListener;
import eu.scape_project.watch.scheduling.quartz.QuartzScheduler;
import eu.scape_project.watch.utils.exceptions.PluginException;

public class QuartzExecutionListenerTest {

  @Ignore
  @Test
  public void testRefireAdaptorWhenFailed() {

    QuartzExecutionListener listener = new QuartzExecutionListener();

    QuartzScheduler scheduler = Mockito.mock(QuartzScheduler.class);
    JobExecutionContext context = Mockito.mock(JobExecutionContext.class);
    JobExecutionException jobException = Mockito.mock(JobExecutionException.class);
    QuartzAdaptorJob adaptorJob = Mockito.mock(QuartzAdaptorJob.class);
    AdaptorPluginInterface adaptor = Mockito.mock(AdaptorPluginInterface.class);

    Mockito.when(adaptor.getName()).thenReturn("Test adaptor");
    Mockito.when(adaptorJob.getAdaptorPlugin()).thenReturn(adaptor);
    Mockito.when(context.getJobInstance()).thenReturn(adaptorJob);
    Mockito.when(context.getResult()).thenReturn(new Boolean(false));
    Mockito.when(context.get("exception")).thenReturn(new PluginException());

    listener.setScheduler(scheduler);

    listener.jobWasExecuted(context, jobException);
    Mockito.verify(scheduler, Mockito.times(1)).executeAdaptor(adaptor, Mockito.any(SourceAdaptorEvent.class));
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    listener.jobWasExecuted(context, jobException);
    Mockito.verify(scheduler, Mockito.times(5)).executeAdaptor(adaptor, Mockito.any(SourceAdaptorEvent.class));
    Mockito.verify(scheduler, Mockito.times(1)).stopAdaptor(adaptor, Mockito.any(SourceAdaptorEvent.class));

  }
}
