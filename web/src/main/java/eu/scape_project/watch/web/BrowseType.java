package eu.scape_project.watch.web;

import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/type/([^/]*)")
@TemplateSource("browseType")
public class BrowseType extends TemplateContext {

  public int getEntityCount() {
    return DAO.ENTITY.countWithType(getId());
  }

  public List<Property> getProperties() {
    return DAO.PROPERTY.listWithType(getId(), 0, getPageSize());
  }

  @Inject
  Matcher m;

  public String getId() {
    return m.group(1);
  }

  public EntityType getEntityType() {
    return DAO.ENTITY_TYPE.findById(getId());
  }
}
