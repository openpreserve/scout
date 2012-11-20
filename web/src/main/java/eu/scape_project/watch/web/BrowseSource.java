package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/source/([^/]*)")
@TemplateSource("browseSource")
public class BrowseSource extends TemplateContext {

  @Inject
  private Matcher m;

  public String getId() {
    return m.group(1);
  }

  public Source getSource() {
    return DAO.SOURCE.findById(getId());
  }

}
