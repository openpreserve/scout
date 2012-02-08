package eu.scape_project.watch.core.model;

import java.util.List;

public class ComposedCondition extends Condition {

  private List<Condition> subcondtions;
  
  private BooleanOperator boolOperator;

  public List<Condition> getSubcondtions() {
    return subcondtions;
  }

  public void setSubcondtions(List<Condition> subcondtions) {
    this.subcondtions = subcondtions;
  }

  public BooleanOperator getBoolOperator() {
    return boolOperator;
  }

  public void setBoolOperator(BooleanOperator boolOperator) {
    this.boolOperator = boolOperator;
  }
  
  
}
