package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import eu.scape_project.watch.core.KB;

/**
 * 
 * Possible targets for a Request/Question.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@XmlType(name = KB.REQUEST_TARGET)
@XmlEnum
public enum RequestTarget {
  /**
   * Target class is {@link EntityType}.
   */
  ENTITY_TYPE,
  /**
   * Target class is {@link Property}.
   */
  PROPERTY,
  /**
   * Target class is {@link Entity}.
   */
  ENTITY,
  /**
   * Target class is {@link PropertyValue}.
   */
  PROPERTY_VALUE;
}
