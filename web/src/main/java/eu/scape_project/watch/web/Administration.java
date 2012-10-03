package eu.scape_project.watch.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
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

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  @Controller
  boolean redirectPostData() throws IOException {
    boolean continueChain = true;
    final String operation = request.getParameter("operation");

    if (operation == null || operation.length() == 0) {
      continueChain = true;
    } else if (operation != null && operation.equals("removeAdaptor")) {
      final String adaptorUID = request.getParameter("adaptor");

      final ServletContext context = ContextUtil.getServletContext(request);
      final AdaptorManager adaptorManager = ContextUtil.getAdaptorManager(context);
      final SourceAdaptor adaptor = adaptorManager.getSourceAdaptor(adaptorUID);

      if (adaptor != null) {
        DAO.delete(adaptor);
        adaptorManager.reloadKnownAdaptors();
        response.sendRedirect(mustacheletPath + "/administration.html");
      } else {
        response.sendError(404, "Source adaptor not found: " + adaptorUID);
      }
      continueChain = false;

    } else {
      response.sendError(400, "Unrecognized operation requested: " + operation);
      continueChain = false;
    }

    return continueChain;
  }

}
