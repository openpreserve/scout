package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.adaptor.c3po.common.C3POProfileReader;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * An abstract command class that fetches specific property values with the
 * provided {@link C3POProfileReader}. Follows the command pattern to avoid many
 * if else ifs.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public abstract class Command {

  /**
   * A profile reader doing the actual work.
   */
  private C3POProfileReader reader;

  /**
   * The property of interest.
   */
  private Property property;

  public void setReader(final C3POProfileReader reader) {
    this.reader = reader;
  }

  public C3POProfileReader getReader() {
    return this.reader;
  }

  public Property getProperty() {
    return this.property;
  }

  public void setProperty(final Property property) {
    this.property = property;
  }

  /**
   * Fetches the {@link PropertyValue} of the property of interest.
   * 
   * @return the value of the property.
   */
  public abstract PropertyValue execute();

}
