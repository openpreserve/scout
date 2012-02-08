package eu.scape_project.watch.core.model;

import java.util.List;

public class SimpleCondition extends Condition {

  private List<Question> questions;
  
  private List<Literal> literals;
  
  private ConditionOperator operator;

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }

  public List<Literal> getLiterals() {
    return literals;
  }

  public void setLiterals(List<Literal> literals) {
    this.literals = literals;
  }

  public ConditionOperator getOperator() {
    return operator;
  }

  public void setOperator(ConditionOperator operator) {
    this.operator = operator;
  }
}
