package eu.scape_project.watch.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.domain.SourceAdaptorEventType;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.interfaces.SchedulerInterface;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/adaptor/new")
@TemplateSource("createSourceAdaptor")
@HttpMethod({HttpMethod.Type.GET, HttpMethod.Type.POST})
public class CreateSourceAdaptor extends TemplateContext {

  private static final String CONFIG_PREFIX = "config.";

  private final Logger log = LoggerFactory.getLogger(getClass());

  public List<PluginInfo> getPlugins() {
    return PluginManager.getDefaultPluginManager().getPluginInfo(PluginType.ADAPTOR);
  }

  public List<Source> getSources() {
    return DAO.SOURCE.query("", 0, getPageSize());
  }

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  @Controller(HttpMethod.Type.POST)
  boolean redirectPostData() throws IOException {
    final String instance = request.getParameter("instance");
    final String plugin = request.getParameter("plugin");
    final String pluginName = plugin.substring(0, plugin.indexOf('|'));
    final String pluginVersion = plugin.substring(plugin.indexOf('|') + 1);
    final String sourceName = request.getParameter("source");

    // Get configuration parameters
    final Map<String, String> configuration = new HashMap<String, String>();
    final Enumeration<String> parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String parameterName = parameterNames.nextElement();
      if (parameterName.startsWith(CONFIG_PREFIX)) {
        String configName = parameterName.substring(CONFIG_PREFIX.length());
        String configValue = request.getParameter(parameterName);
        log.debug("Configuration parameter {}: {}", new Object[] {configName, configValue});
        configuration.put(configName, configValue);
      }
    }

    final Source source = DAO.SOURCE.findByName(sourceName);

    if (source != null) {
      final ServletContext context = ContextUtil.getServletContext(request);
      final AdaptorManager adaptorManager = ContextUtil.getAdaptorManager(context);
      final SchedulerInterface scheduler = ContextUtil.getScheduler(context);

      final SourceAdaptor adaptor = adaptorManager.createAdaptor(pluginName, pluginVersion, instance, configuration,
        source);

      if (adaptor != null) {
        final AdaptorPluginInterface adaptorInstance = adaptorManager.getAdaptorInstance(adaptor.getInstance());
        scheduler.start(adaptorInstance, new SourceAdaptorEvent(SourceAdaptorEventType.STARTED, "First run"));

        response.sendRedirect(getMustacheletPath() + "/administration");
      } else {
        // TODO send error of plugin does not exist.
        response.sendError(404, "Plug-in does not exist: " + StringEscapeUtils.escapeHtml(pluginName) + "-"
          + StringEscapeUtils.escapeHtml(pluginVersion));
      }
    } else {
      // TODO send error source does not exist.
      response.sendError(404, "Source does not exist: " + StringEscapeUtils.escapeHtml(sourceName));
    }

    return false;
  }
}
