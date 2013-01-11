package eu.scape_project.watch.domain;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.utils.KBUtils;
import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.QUESTION_TEMPLATE_PARAMETER)
@XmlAccessorType(XmlAccessType.FIELD)
public class QuestionTemplateParameter extends RdfBean<QuestionTemplateParameter> {

  public enum ParameterType {
    NODE, LITERAL;
  }

  /**
   * The unique identifier of the question template.
   */
  @Id
  @XmlElement(required = true)
  private String id;

  private String sparqlVariable;

  private String label;

  private String description;

  private ParameterType parameterType;

  private String nodeFilterSparql;

  private RequestTarget nodeFilterTarget;

  private DataType literalFilterDatatype;

  private RenderingHint literalFilterRenderingHint;

  public QuestionTemplateParameter() {
    super();
  }

  public QuestionTemplateParameter(final String sparqlVariable, final String label, final String description,
    final ParameterType parameterType, final String nodeFilterSparql, final RequestTarget nodeFilterTarget,
    final DataType literalFilterDatatype, final RenderingHint literalFilterRenderingHint) {
    super();
    this.id = UUID.randomUUID().toString();
    this.sparqlVariable = sparqlVariable;
    this.label = label;
    this.description = description;
    this.parameterType = parameterType;
    this.nodeFilterSparql = nodeFilterSparql;
    this.nodeFilterTarget = nodeFilterTarget;
    this.literalFilterDatatype = literalFilterDatatype;
    this.literalFilterRenderingHint = literalFilterRenderingHint;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSparqlVariable() {
    return sparqlVariable;
  }

  public void setSparqlVariable(String sparqlVariable) {
    this.sparqlVariable = sparqlVariable;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ParameterType getParameterType() {
    return parameterType;
  }

  public void setParameterType(ParameterType parameterType) {
    this.parameterType = parameterType;
  }

  public String getNodeFilterSparql() {
    return nodeFilterSparql;
  }

  public void setNodeFilterSparql(String nodeFilterSparql) {
    this.nodeFilterSparql = nodeFilterSparql;
  }

  public RequestTarget getNodeFilterTarget() {
    return nodeFilterTarget;
  }

  public void setNodeFilterTarget(RequestTarget nodeFilterTarget) {
    this.nodeFilterTarget = nodeFilterTarget;
  }

  public DataType getLiteralFilterDatatype() {
    return literalFilterDatatype;
  }

  public void setLiteralFilterDatatype(DataType literalFilterDatatype) {
    this.literalFilterDatatype = literalFilterDatatype;
  }

  public RenderingHint getLiteralFilterRenderingHint() {
    return literalFilterRenderingHint;
  }

  public void setLiteralFilterRenderingHint(RenderingHint literalFilterRenderingHint) {
    this.literalFilterRenderingHint = literalFilterRenderingHint;
  }

}
