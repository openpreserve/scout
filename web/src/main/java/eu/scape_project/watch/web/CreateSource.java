package eu.scape_project.watch.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/source/new")
@Template("createSource.html")
@HttpMethod({HttpMethod.Type.GET, HttpMethod.Type.POST})
public class CreateSource extends Mustachelet {

  boolean page_administration() {
    return true;
  }

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  @Controller(HttpMethod.Type.POST)
  boolean redirectPostData() throws IOException {
    final String name = request.getParameter("name");
    final String description = request.getParameter("description");
    final String redirect = request.getParameter("redirect");

    // TODO check if name already exists?
    final Source source = DAO.SOURCE.save(new Source(name, description));

    if (source != null) {
      response.sendRedirect(mustacheletPath + redirect);
    } else {
      // TODO send error of source could not be created.
    }

    return false;
  }

  public String redirect() {
    // TODO get redirect from query string in GET.
    return "/adaptor/new";
  }

}
