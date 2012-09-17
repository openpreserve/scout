package eu.scape_project.watch.web;

import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/browse/type/([^/]*)")
@Template("browseType.html")
public class BrowseType extends Mustachelet {

  private static final int PAGE_SIZE = 100;

  public boolean page_browse() {
    return true;
  }

  public List<Entity> entity() {
    return DAO.ENTITY.listWithType(typeName(), 0, PAGE_SIZE);
  }

  public List<Property> property() {
    return DAO.PROPERTY.listWithType(typeName(), 0, PAGE_SIZE);
  }

  @Inject
  Matcher m;

  String typeName() {
    return m.group(1);
  }

  EntityType entityType() {
    return DAO.ENTITY_TYPE.findById(typeName());
  }
}
