package eu.scape_project.watch.adaptor.c3po.command;

import eu.scape_project.watch.adaptor.c3po.common.C3POProfileReader;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_DESCRIPTION;
import static eu.scape_project.watch.adaptor.c3po.common.C3POConstants.CP_NAME;

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

  public void setReader(final C3POProfileReader reader) {
    this.reader = reader;
  }

  public C3POProfileReader getReader() {
    return this.reader;
  }

  /**
   * Retrieves a property object with the default datatype and the given name
   * and description. The Entity Type is automatically set to point to a
   * collection profile entity type.
   * 
   * @param name
   *          the name of the property.
   * @param desc
   *          the human readable description of the property.
   * @return the {@link Property} object.
   */
  public Property getProperty(final String name, final String desc) {
    final EntityType cp = new EntityType(CP_NAME, CP_DESCRIPTION);
    return new Property(cp, name, desc);
  }

  /**
   * Fetches the {@link PropertyValue} of the property of interest.
   * 
   * @return the value of the property.
   */
  public abstract PropertyValue execute();

}
