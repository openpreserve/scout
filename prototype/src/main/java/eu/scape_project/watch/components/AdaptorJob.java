package eu.scape_project.watch.components;

import java.util.Properties;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IAdaptorJob;
import eu.scape_project.watch.core.plugin.PluginException;
import eu.scape_project.watch.core.plugin.PluginManager;

public abstract class AdaptorJob implements IAdaptorJob {

  private String adaptorClassName;
  
  private String adaptorVersion; 
  
  private Properties properties;
  
  @Override
  public void execute(JobExecutionContext jec) throws JobExecutionException {
    // TODO Auto-generated method stub
    //LOG.info("adaptor job running");
    IAdaptor adaptor = (IAdaptor) PluginManager.getDefaultPluginManager()
                                    .getPlugin(adaptorClassName, adaptorVersion);
    try {
      jec.setResult(adaptor.execute());
    } catch (PluginException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  
  @Override
  public void setAdaptorClassName(String className) {
    this.adaptorClassName = className;
  }
  
  @Override
  public void setAdaptorVersion(String version){
    this.adaptorVersion = version;
  }
  
  @Override
  public void initialize(Properties properties) {
    this.properties = properties;
    initialize();
  }
  
  
  protected Properties getProperties() {
    return properties;
  }
  
  protected abstract void initialize();
}
