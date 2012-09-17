package eu.scape_project.watch.web;

import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/browse/entity/([^/]*)")
@Template("browseEntity.html")
public class BrowseEntity extends Mustachelet {


  public boolean page_browse() {
    return true;
  }

  public List<Property> property() {
    return DAO.PROPERTY.listWithType(entity().getEntityType().getName(), 0, 100);
  }

  @Controller
  public PropertyValue getValue(String propertyName) {
    return DAO.PROPERTY_VALUE.find(entityName(), entity().getEntityType().getName(), propertyName);
  }
  
  public List<PropertyValue> value() {
    return DAO.PROPERTY_VALUE.listWithEntity(entityName(), 0, 100);
  }

  @Inject
  Matcher m;

  String entityName() {
    return m.group(1);
  }

  Entity entity() {
    return DAO.ENTITY.findById(entityName());
  }
}
