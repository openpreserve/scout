package eu.scape_project.watch.web;

import java.util.List;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/browse/entity/([^/]*)/([^/]*)")
@Template("browseEntity.html")
public class BrowseEntity extends Mustachelet {

  private Logger log = LoggerFactory.getLogger(getClass());

  public boolean page_browse() {
    return true;
  }

  public List<Property> property() {
    return DAO.PROPERTY.listWithType(entity().getType().getName(), 0, 100);
  }

  public List<PropertyValue> value() {
    return DAO.PROPERTY_VALUE.listWithEntity(entity(), 0, 100);
  }

  @Inject
  private Matcher m;

  public String typeName() {
    return m.group(1);
  }

  public String entityName() {
    return m.group(2);
  }

  public Entity entity() {
    return DAO.ENTITY.findById(typeName(), entityName());
  }
}
