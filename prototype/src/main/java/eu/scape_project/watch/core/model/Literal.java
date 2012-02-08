package eu.scape_project.watch.core.model;


public class Literal {
  
  private String value;

  public Literal() {
    super();
  }
  
  public Literal(String v) {
    this.value = v;
  }
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
