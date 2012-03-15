package eu.scape_project.watch.components;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IAdaptorJob;

public abstract class AdaptorJob implements IAdaptorJob {

  private IAdaptor adaptor;

  @Override
  public void execute(JobExecutionContext jec) throws JobExecutionException {
    // TODO Auto-generated method stub
    //LOG.info("adaptor job running");
    jec.setResult(adaptor.execute());
  }

  
  @Override
  public void setAdaptor(String className) {

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
