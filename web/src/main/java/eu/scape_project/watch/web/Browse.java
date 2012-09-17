package eu.scape_project.watch.web;

import java.util.List;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/browse.html")
@Template("browse.html")
public class Browse extends Mustachelet {
  
  private static final int PAGE_SIZE = 100;
  
  public boolean page_browse() {
    return true;
  }

  public List<EntityType> entitytype() {
    return DAO.ENTITY_TYPE.query("", 0, PAGE_SIZE);
  }
  
  
}
