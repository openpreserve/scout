package eu.scape_project.watch.scheduling.quartz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorJobInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

public abstract class AdaptorJob implements AdaptorJobInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AdaptorJob.class);

  private String adaptorClassName;

  private String adaptorVersion;

  private Properties properties;

  private String adaptorProperties;

  @Override
  public void execute(final JobExecutionContext jec) throws JobExecutionException {

    // TODO integrate this part with PluginManager
    final AdaptorPluginInterface adaptor = (AdaptorPluginInterface) PluginManager.getDefaultPluginManager().getPlugin(
      adaptorClassName, adaptorVersion);

    if (adaptor == null) {
      LOG.warn("No adaptor found in the plugin manager, skipping.");
    } else {
      try {
        LOG.trace("properties: {}", adaptorProperties);
        properties = new Properties();
        properties.load(new ByteArrayInputStream(adaptorProperties.getBytes("UTF-8")));
        LOG.trace("properties size {}", properties.keySet().size());
        Map<String, String> map = new HashMap<String, String>();
        for (Object key : properties.keySet()) {
          map.put(key.toString(), properties.getProperty(key.toString()));
          LOG.trace("key {}, value {}", key.toString(), properties.getProperty(key.toString()));
        }

        //TODO improve this part 
        adaptor.setParameterValues(map);
        jec.setResult(adaptor.execute());
      } catch (PluginException e) {
        
      } catch (IOException e) {
        
      } catch (InvalidParameterException e) {
        
      }
    }

  }

  @Override
  public void setAdaptorClassName(String className) {
    this.adaptorClassName = className;
  }

  @Override
  public String getAdaptorClassName() {
    return this.adaptorClassName;
  }

  @Override
  public void setAdaptorVersion(String version) {
    this.adaptorVersion = version;
  }

  @Override
  public String getAdaptorVersion() {
    return this.adaptorVersion;
  }

  @Override
  public void setAdaptorProperties(String properties) {
    this.adaptorProperties = properties;
  }

  @Override
  public void initialize(Properties properties) {
    this.properties = properties;
    this.initialize();
  }

  protected Properties getProperties() {
    return properties;
  }

  protected abstract void initialize();

  
  @Override
  public boolean equals(Object object) {
    if (object==null) {
      return false;
    }
    if (this == object){
      return true;
    }
    if (this.getClass() != object.getClass()){
      return false;
    }
    AdaptorJob aJob = (AdaptorJob) object;
    return this.getAdaptorClassName().equals(aJob.getAdaptorClassName())
      && this.getAdaptorVersion().equals(aJob.getAdaptorVersion());
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.adaptorClassName == null) ? 0 : this.adaptorClassName.hashCode());
    result = prime * result + ((this.adaptorVersion == null) ? 0 : this.adaptorVersion.hashCode());
    result = prime * result + ((this.adaptorProperties == null) ? 0 : this.adaptorProperties.hashCode());
    result = prime * result + ((this.properties == null) ? 0 : this.properties.hashCode());
    return result;
  }
  
}
