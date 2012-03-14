package eu.scape_project.watch.components;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IAdaptorJob;

public class AdaptorJob implements IAdaptorJob  {

  private static final Logger LOG = LoggerFactory.getLogger(AdaptorJob.class);
  
  private IAdaptor adaptor;
  
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



  @Override
  public void setAdaptor(String className)  {
    
      try {
        adaptor = (IAdaptor) Class.forName(className).newInstance();
      } catch (InstantiationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    
    
  }
  

}
