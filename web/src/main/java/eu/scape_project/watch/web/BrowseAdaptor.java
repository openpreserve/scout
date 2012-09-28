package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/browse/adaptor/([^/]*)")
@Template("browseAdaptor.html")
public class BrowseAdaptor extends Mustachelet {

  public boolean page_browse() {
    return true;
  }


  @Inject
  Matcher m;

  String instance() {
    return m.group(1);
  }
  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;
  
  public SourceAdaptor adaptor() {
    final ServletContext context = ContextUtil.getServletContext(request);
    final AdaptorManager adaptorManager = ContextUtil.getAdaptorManager(context);
    return adaptorManager.getSourceAdaptor(instance());
  }
}
