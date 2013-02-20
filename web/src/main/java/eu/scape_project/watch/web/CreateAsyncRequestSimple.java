package eu.scape_project.watch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.QueryBinding;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.QuestionTemplate;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/request/new")
@TemplateSource("createAsyncRequestSimple")
@HttpMethod({HttpMethod.Type.GET, HttpMethod.Type.POST})
public class CreateAsyncRequestSimple extends TemplateContext {

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  @Controller(HttpMethod.Type.POST)
  boolean redirectPostData() throws IOException {
    final String description = request.getParameter("description");
    final String templateId = request.getParameter("template");
    final String[] bindingsArray = request.getParameterValues("binding");
    final String email = request.getParameter("email");
    final String periodOption = request.getParameter("periodOptions");
    final String planId = request.getParameter("plan");
    
    final QuestionTemplate template = DAO.QUESTION_TEMPLATE.findById(templateId);
    
    final List<QueryBinding> bindings = new ArrayList<QueryBinding>();
    for(String unparsedBinding : bindingsArray) {
      bindings.add(QueryBinding.valueOf(unparsedBinding));
    }
    
    long period;

    if (periodOption.equals("daily")) {
      period = 86400000L;
    } else if (periodOption.equals("weekly")) {
      period = 604800000L;
    } else if (periodOption.equals("monthly")) {
      period = 2630000000L;
    } else if (periodOption.equals("quarterly")) {
      period = 10528000000L;
    } else if (periodOption.equals("yearly")) {
      period = 31555690000L;
    } else {
      period = 0L;
    }

    Plan plan = null;
    if (StringUtils.isNotBlank(planId)) {
      plan = new Plan(planId);
    }

    if (StringUtils.isBlank(description)) {
      response.sendError(400, "Please introduce a name for your trigger");
    } else if(template == null) {
      response.sendError(404, "Question template not found");
    } else if (period == 0) {
      response.sendError(400, "Select a scheduling period or a category, entity or property initiator");
    } else if (StringUtils.isBlank(email)) {
      response.sendError(400, "Select an email where to send the notification");
    } else {
      final Question question = new Question(template.getSparql(), bindings, template.getTarget());
      final Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("recipient", email);
      final Notification notification = new Notification("email", parameters);
      final Trigger trigger = new Trigger(null, null, null, period, question, plan, Arrays.asList(notification));
      final AsyncRequest request = new AsyncRequest(description, Arrays.asList(trigger));
      DAO.save(request);

      response.sendRedirect(getMustacheletPath() + "/browse/request/" + request.getId());
    }

    return false;
  }
}
