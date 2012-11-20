package eu.scape_project.watch.web;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/edit/source/([^/]*)")
@TemplateSource("editSource")
@HttpMethod({HttpMethod.Type.GET, HttpMethod.Type.POST})
public class EditSource extends TemplateContext {

  @Inject
  private Matcher m;

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  @Controller(HttpMethod.Type.POST)
  boolean redirectPostData() throws IOException {
    final String description = request.getParameter("description");
    final String redirect = request.getParameter("redirect");

    Source source = DAO.SOURCE.findById(getId());
    source.setDescription(description);
    source = DAO.SOURCE.save(source);

    if (source != null) {
      response.sendRedirect(getMustacheletPath() + redirect);
    } else {
      // TODO send error of source could not be updated.
    }

    return false;
  }

  public String getId() {
    return m.group(1);
  }

  public Source getSource() {
    return DAO.SOURCE.findById(getId());
  }

  public String getRedirect() {
    return "/browse/source/" + this.getId();
  }

}
