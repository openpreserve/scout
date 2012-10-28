package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/value/([^/]*)")
@TemplateSource("browseValue")
public class BrowseValue extends TemplateContext {

  public PropertyValue getPropertyValue() {
    return DAO.PROPERTY_VALUE.findById(getId());
  }

  public int getPropertyValueVersionCount() {
    final PropertyValue propertyValue = getPropertyValue();
    return DAO.PROPERTY_VALUE.countWithEntityAndProperty(propertyValue.getEntity().getId(), propertyValue.getProperty()
      .getId());
  }

  public int getVersionMeasurementCount() {
    return DAO.MEASUREMENT.countByPropertyValue(getPropertyValue());
  }

  public int getVersionSourceCount() {
    return DAO.SOURCE.countByPropertyValue(getPropertyValue());
  }

  @Inject
  private Matcher m;

  public String getId() {
    return m.group(1);
  }

}
