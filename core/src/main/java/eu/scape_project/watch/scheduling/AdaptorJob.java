package eu.scape_project.watch.scheduling;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.scape_project.watch.interfaces.AdaptorJobInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

public abstract class AdaptorJob implements AdaptorJobInterface {

  private String adaptorClassName;

  private String adaptorVersion;

  private Properties properties;

  private String adaptorProperties;
  
  @Override
  public void execute(final JobExecutionContext jec) throws JobExecutionException {

    // TODO integrate this part with PluginManager
    try {
      final AdaptorPluginInterface adaptor = (AdaptorPluginInterface) PluginManager.getDefaultPluginManager()
                                                                        .getPlugin(adaptorClassName, adaptorVersion);
      Properties properties = new Properties();
      properties.load(new ByteArrayInputStream(adaptorProperties.getBytes("UTF-8")));
      Map<String,String> map = new HashMap<String, String>();
      for (Object key : properties.keySet()) {
        map.put(key.toString(), properties.getProperty(key.toString()));
      }
      adaptor.setParameterValues(map);
      jec.setResult(adaptor.execute());
    } catch (PluginException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidParameterException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Override
  public void setAdaptorClassName(String className) {
    this.adaptorClassName = className;
  }

  @Override
  public void setAdaptorVersion(String version) {
    this.adaptorVersion = version;
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
}
