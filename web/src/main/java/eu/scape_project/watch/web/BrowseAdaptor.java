package eu.scape_project.watch.web;

import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/adaptor/([^/]*)")
@TemplateSource("browseAdaptor")
public class BrowseAdaptor extends TemplateContext {

  @Inject
  Matcher m;

  String getId() {
    return m.group(1);
  }

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  public SourceAdaptor getAdaptor() {
    // final ServletContext context = ContextUtil.getServletContext(request);
    // final AdaptorManager adaptorManager =
    // ContextUtil.getAdaptorManager(context);
    // return adaptorManager.getSourceAdaptor(instance());
    return DAO.SOURCE_ADAPTOR.findById(getId());
  }

  public List<SourceAdaptorEvent> getEvents() {
    return DAO.SOURCE_ADAPTOR_EVENT.listByAdaptor(getAdaptor(), 0, getPageSize());
  }

  public int getEventCount() {
    return DAO.SOURCE_ADAPTOR_EVENT.countByAdaptor(getAdaptor());
  }

}
