package eu.scape_project.watch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/request/new")
@TemplateSource("createAsyncRequest")
@HttpMethod({HttpMethod.Type.GET, HttpMethod.Type.POST})
public class CreateAsyncRequest extends TemplateContext {

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  @Controller(HttpMethod.Type.POST)
  boolean redirectPostData() throws IOException {
    final String description = request.getParameter("description");
    final String target = request.getParameter("target");
    final String sparql = request.getParameter("query");
    final String email = request.getParameter("email");

    final Question question = new Question(sparql, RequestTarget.valueOf(target.toUpperCase()), null, null, null,
      3600000L);
    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("recipient", email);
    final Notification notification = new Notification("email", parameters);
    final Trigger trigger = new Trigger(question, Arrays.asList(notification), null);
    final AsyncRequest request = new AsyncRequest(description, Arrays.asList(trigger));
    DAO.save(request);

    response.sendRedirect(getMustacheletPath() + "/browse/request/" + request.getId());

    return false;
  }

}
