package eu.scape_project.watch.web;

import java.util.List;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/dashboard")
@TemplateSource("dashboard")
public class Dashboard extends TemplateContext {

  public List<AsyncRequest> getRequests() {
    return DAO.ASYNC_REQUEST.list(0, getPageSize());
  }

}
