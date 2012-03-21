package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.adaptor.c3po.C3POProfileReader;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

public abstract class Command {

  private C3POProfileReader reader;

  private Property property;

  public void setReader(C3POProfileReader reader) {
    this.reader = reader;
  }

  public C3POProfileReader getReader() {
    return reader;
  }

  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public abstract PropertyValue execute();

}
