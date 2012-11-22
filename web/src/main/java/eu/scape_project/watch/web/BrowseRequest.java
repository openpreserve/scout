package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/request/([^/]*)")
@TemplateSource("browseRequest")
public class BrowseRequest extends TemplateContext {

  @Inject
  Matcher m;

  public String getId() {
    return m.group(1);
  }

  public AsyncRequest getRequest() {
    return DAO.ASYNC_REQUEST.findById(getId());
  }
}
