package eu.scape_project.watch.adaptor.pronom;

import java.util.List;
import java.util.Map;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

public class PronomAdaptor implements AdaptorPluginInterface {

  private static final String HOST = "http://test.linkeddatapronom.nationalarchives.gov.uk";

  public static final String ENDPOINT = HOST + "/sparql/endpoint.php";

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
    // TODO Auto-generated method stub
    return null;
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

}
