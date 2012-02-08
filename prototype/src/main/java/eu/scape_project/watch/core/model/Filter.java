package eu.scape_project.watch.core.model;

import java.util.List;

public class Filter {

  private String parameterId;

  private List<Literal> literals;

  private ConditionOperator operator;

  private FilterParameter filterParameter;

  public FilterParameter getFilterParameter() {
    return filterParameter;
  }

  public void setFilterParameter(FilterParameter filterParameter) {
    this.filterParameter = filterParameter;
  }

  public ConditionOperator getOperator() {
    return operator;
  }

  public void setOperator(ConditionOperator operator) {
    this.operator = operator;
  }

  public List<Literal> getLiterals() {
    return literals;
  }

  public void setLiterals(List<Literal> literals) {
    this.literals = literals;
  }

  public String getParameterId() {
    return parameterId;
  }

  public void setParameterId(String parameterId) {
    this.parameterId = parameterId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((filterParameter == null) ? 0 : filterParameter.hashCode());
    result = prime * result + ((literals == null) ? 0 : literals.hashCode());
    result = prime * result + ((operator == null) ? 0 : operator.hashCode());
    result = prime * result + ((parameterId == null) ? 0 : parameterId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Filter other = (Filter) obj;
    if (filterParameter != other.filterParameter)
      return false;
    if (literals == null) {
      if (other.literals != null)
        return false;
    } else if (!literals.equals(other.literals))
      return false;
    if (operator != other.operator)
      return false;
    if (parameterId == null) {
      if (other.parameterId != null)
        return false;
    } else if (!parameterId.equals(other.parameterId))
      return false;
    return true;
  }

}
