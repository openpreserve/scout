package eu.scape_project.watch.dao;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.RequestTarget;

import java.util.List;

import thewebsemantic.binding.RdfBean;

/**
 * 
 * Data Access Object for synchronous requests.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class RequestDAO extends AbstractDO {

  /**
   * Singleton instance.
   */
  private static RequestDAO instance = null;

  /**
   * Get singleton instance.
   * 
   * @return The singleton instance
   */
  public static RequestDAO getInstance() {
    if (instance == null) {
      instance = new RequestDAO();
    }
    return instance;
  }

  /**
   * No other instances can exist as this is a singleton.
   */
  private RequestDAO() {

  }

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
  @SuppressWarnings("unchecked")
  public List<? extends RdfBean<?>> query(final RequestTarget target, final String query, final int start, final int max) {
    List<? extends RdfBean<?>> ret;
    switch (target) {
      case ENTITY_TYPE:
        ret = super.query(EntityType.class, query, start, max);
        break;
      case PROPERTY:
        ret = super.query(Property.class, query, start, max);
        break;
      case ENTITY:
        ret = super.query(Entity.class, query, start, max);
        break;
      case PROPERTY_VALUE:
        ret = super.query(PropertyValue.class, query, start, max);
        break;
      default:
        ret = null;
        break;
    }
    return ret;
  }

}
