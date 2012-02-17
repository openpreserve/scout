package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;
import eu.scape_project.watch.core.model.RequestTarget;

import java.util.List;

import thewebsemantic.binding.RdfBean;

/**
 * 
 * Data Access Object for synchronous requests.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class RequestDAO extends AbstractDAO {

  /**
   * Make a synchronous request to the KB.
   * 
   * @param target
   *          The request target
   * @param query
   *          The request query
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * 
   * @return A list of resources, of the type defined in the target, filtered by
   *         the above constraints
   */
  public static List<? extends RdfBean<?>> query(final RequestTarget target, final String query, final int start,
    final int max) {
    List<? extends RdfBean<?>> ret;
    switch (target) {
      case ENTITY_TYPE:
        ret = query(EntityType.class, query, start, max);
        break;
      case PROPERTY:
        ret = query(Property.class, query, start, max);
        break;
      case ENTITY:
        ret = query(Entity.class, query, start, max);
        break;
      case PROPERTY_VALUE:
        ret = query(PropertyValue.class, query, start, max);
        break;
      default:
        ret = null;
        break;
    }
    return ret;
  }

}
