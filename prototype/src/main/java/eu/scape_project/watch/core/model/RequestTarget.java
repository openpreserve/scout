package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import thewebsemantic.binding.RdfBean;

import eu.scape_project.watch.core.KBUtils;

/**
 * 
 * Possible targets for a Request/Question.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@XmlType(name = KBUtils.REQUEST_TARGET)
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

  /**
   * Get request target based on the target class it refers to.
   * 
   * @param <T>
   *          A class that must extends RdfBean and should be related to
   *          possible targets of a request.
   * @param targetClass
   *          The target class.
   * @return The request target value or null if not found.
   */
  public static RequestTarget getTargetByClass(final Class<? extends RdfBean<?>> targetClass) {
    RequestTarget ret = null;
    if (targetClass == EntityType.class) {
      ret = ENTITY_TYPE;
    } else if (targetClass == Property.class) {
      ret = PROPERTY;
    } else if (targetClass == Entity.class) {
      ret = ENTITY;
    } else if (targetClass == PropertyValue.class) {
      ret = PROPERTY_VALUE;
    }

    return ret;
  }

  /**
   * Get the class the target refers to.
   * 
   * @param target
   *          The request target.
   * @return The class or null if not found.
   */
  public static Class<? extends RdfBean<?>> getClassByTarget(final RequestTarget target) {
    Class<? extends RdfBean<?>> ret;
    switch (target) {
      case ENTITY_TYPE:
        ret = EntityType.class;
        break;
      case PROPERTY:
        ret = Property.class;
        break;
      case ENTITY:
        ret = Entity.class;
        break;
      case PROPERTY_VALUE:
        ret = PropertyValue.class;
        break;
      default:
        ret = null;
        break;
    }

    return ret;
  }

  /**
   * Get the class this target refers to.
   * 
   * @return The class or null if not found.
   */
  public Class<? extends RdfBean<?>> getTargetClass() {
    return getClassByTarget(this);
  }

}
