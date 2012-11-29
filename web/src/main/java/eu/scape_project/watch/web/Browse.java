package eu.scape_project.watch.web;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Objective;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.policy.PolicyModel;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse")
@TemplateSource("browse")
public class Browse extends TemplateContext {
  
  public List<EntityType> getEntityTypes() {
    return DAO.ENTITY_TYPE.query("", 0, getPageSize());
  }

  public int getEntityTypeCount() {
    return DAO.ENTITY_TYPE.count("");
  }

}
