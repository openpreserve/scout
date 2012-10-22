package eu.scape_project.watch.web;

import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/entity/([^/]*)/([^/]*)")
@TemplateSource("browseEntity")
public class BrowseEntity extends TemplateContext {

  public List<Property> getProperties() {
    return DAO.PROPERTY.listWithType(getEntity().getType().getName(), 0, 100);
  }

  public List<PropertyValue> getValues() {
    return DAO.PROPERTY_VALUE.listWithEntity(getEntity(), 0, 100);
  }

  @Inject
  private Matcher m;

  public String getTypeName() {
    return m.group(1);
  }

  public String getEntityName() {
    return m.group(2);
  }

  public Entity getEntity() {
    return DAO.ENTITY.findById(getTypeName(), getEntityName());
  }
}
