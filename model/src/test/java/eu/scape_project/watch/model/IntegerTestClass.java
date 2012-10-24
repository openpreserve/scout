package eu.scape_project.watch.model;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

@Namespace("test")
public class IntegerTestClass extends RdfBean<IntegerTestClass> {

  private String id;
  private Integer integerField;

  public IntegerTestClass() {
    super();
  }

  public IntegerTestClass(String id, Integer longField) {
    super();
    this.id = id;
    this.integerField = longField;
  }

  @Id
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getIntegerField() {
    return integerField;
  }

  public void setIntegerField(Integer integerField) {
    this.integerField = integerField;
  }
}
