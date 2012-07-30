package eu.scape_project.watch.core.scheduling.quartz.integration;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

public class AdaptorPluginMock2 implements AdaptorPluginInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AdaptorPluginMock2.class);
  
  @Override
  public void init() throws PluginException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void shutdown() throws PluginException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getName() {
    return "AdaptorMock2";
  }

  @Override
  public String getVersion() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PluginType getPluginType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ConfigParameter> getParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, String> getParameterValues() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setParameterValues(Map<String, String> values) throws InvalidParameterException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public ResultInterface execute(Map<Entity, List<Property>> config) throws PluginException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResultInterface execute() throws PluginException {
    // TODO Auto-generated method stub
    return null;
  }

  private int count = 0;
  @Override
  public boolean hasNext() throws PluginException {
    LOG.info("fetching the data from the source " + count + ".");
    if (count>1)
      throw new PluginException("this adaptor is fake");
    count++;
    return false;
  }

  @Override
  public ResultInterface next() {
    // TODO Auto-generated method stub
    return null;
  }

}
