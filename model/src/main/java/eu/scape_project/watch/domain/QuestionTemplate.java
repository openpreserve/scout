package eu.scape_project.watch.domain;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.utils.KBUtils;

@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.QUESTION_TEMPLATE)
@XmlAccessorType(XmlAccessType.FIELD)
public class QuestionTemplate extends RdfBean<QuestionTemplate> {

  /**
   * The unique identifier of the question template.
   */
  @Id
  @XmlElement(required = true)
  private String id;

  private String title;

  private String description;

  private String sparql;

  private RequestTarget target;

  private List<QuestionTemplateParameter> parameters;

  public QuestionTemplate() {
    super();
  }

  public QuestionTemplate(final String title, final String description, final String sparql,
    final RequestTarget target, final List<QuestionTemplateParameter> parameters) {
    super();
    this.id = UUID.randomUUID().toString();
    this.title = title;
    this.description = description;
    this.sparql = sparql;
    this.target = target;
    this.parameters = parameters;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSparql() {
    return sparql;
  }

  public void setSparql(String sparql) {
    this.sparql = sparql;
  }

  public RequestTarget getTarget() {
    return target;
  }

  public void setTarget(RequestTarget target) {
    this.target = target;
  }

  public List<QuestionTemplateParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<QuestionTemplateParameter> parameters) {
    this.parameters = parameters;
  }

}
