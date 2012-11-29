package eu.scape_project.watch.web;

import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
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

  public List<SourceAdaptor> getAdaptors() {
    final Source source = getSource();
    int count = DAO.SOURCE_ADAPTOR.countBySource(source);
    return DAO.SOURCE_ADAPTOR.listBySource(source, 0, count);
  }

}
