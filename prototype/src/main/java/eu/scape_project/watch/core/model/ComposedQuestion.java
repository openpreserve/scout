package eu.scape_project.watch.core.model;

import java.util.List;

public class ComposedQuestion extends Question {

  private List<Question> subquestions;
  
  private AlgebraicOperator operator;

  public List<Question> getSubquestions() {
    return subquestions;
  }

  public void setSubquestions(List<Question> subquestions) {
    this.subquestions = subquestions;
  }

  public AlgebraicOperator getOperator() {
    return operator;
  }

  public void setOperator(AlgebraicOperator operator) {
    this.operator = operator;
  }
}
