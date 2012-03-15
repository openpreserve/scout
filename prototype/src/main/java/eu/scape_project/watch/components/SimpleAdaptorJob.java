package eu.scape_project.watch.components;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IAdaptorJob;

public class SimpleAdaptorJob extends AdaptorJob  {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleAdaptorJob.class);
  
  
  @Override
  public void execute(JobExecutionContext arg0) throws JobExecutionException {
    // TODO Auto-generated method stub
    LOG.info("adaptor job running");
    adaptor.execute();
  }

  @Override
  public Result getResult() {
    // TODO Auto-generated method stub
    return null;
  }



    
    
  }
  

}
