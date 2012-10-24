package eu.scape_project.watch.model;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

@Namespace("test")
public class LongTestClass extends RdfBean<LongTestClass> {

  private String id;

  private long longField;

  public LongTestClass() {
    super();
  }

  public LongTestClass(String id, long longField) {
    super();
    this.id = id;
    this.longField = longField;
  }

  @Id
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getLongField() {
    return longField;
  }

  public void setLongField(long longField) {
    this.longField = longField;
  }
}
