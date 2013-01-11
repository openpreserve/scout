package eu.scape_project.watch.web;

import java.util.List;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.QuestionTemplate;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/query")
@TemplateSource("querySimple")
public class QuerySimple extends TemplateContext {

  public List<QuestionTemplate> getQuestionTemplates() {
    return DAO.QUESTION_TEMPLATE.list(0, getPageSize());
  }

}
