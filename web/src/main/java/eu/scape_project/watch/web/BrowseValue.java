package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/value/([^/]*)/([^/]*)/([^/]*)/(\\d+)")
@TemplateSource("browseValue")
public class BrowseValue extends TemplateContext {

  public PropertyValue getPropertyValue() {
    return DAO.PROPERTY_VALUE.find(getTypeName(), getEntityName(), getPropertyName(), getVersion());
  }
  
  public int getPropertyValueVersionCount() {
    return DAO.PROPERTY_VALUE.countWithEntityAndProperty(getTypeName(), getEntityName(), getPropertyName());
  }
  
  public int getVersionMeasurementCount() {
    return DAO.MEASUREMENT.countByPropertyValue(getPropertyValue());
  }
  
  public int getVersionSourceCount() {
    return DAO.SOURCE.countByPropertyValue(getPropertyValue());
  }

  @Inject
  private Matcher m;

  public String getTypeName() {
    return m.group(1);
  }

  public String getEntityName() {
    return m.group(2);
  }

  public String getPropertyName() {
    return m.group(3);
  }

  public int getVersion() {
    return Integer.parseInt(m.group(4));
  }

}
