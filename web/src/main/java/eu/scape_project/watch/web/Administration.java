package eu.scape_project.watch.web;

import java.util.List;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/administration.html")
@Template("administration.html")
public class Administration extends Mustachelet {

  boolean page_administration() {
    return true;
  }

  public List<SourceAdaptor> sourceadaptor() {
    return DAO.SOURCE_ADAPTOR.queryAll(0, PAGE_SIZE);
  }

  public List<PluginInfo> plugin() {
    return PluginManager.getDefaultPluginManager().getPluginInfo();
  }

}
