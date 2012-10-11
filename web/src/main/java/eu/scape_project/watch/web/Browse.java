package eu.scape_project.watch.web;

import java.util.List;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse")
@TemplateSource("browse")
public class Browse extends TemplateContext {
  
  public List<EntityType> getEntityTypes() {
    return DAO.ENTITY_TYPE.query("", 0, getPageSize());
  }
  
  
}
