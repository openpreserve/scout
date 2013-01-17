package eu.scape_project.watch.dao;

import java.util.List;

import com.hp.hpl.jena.query.QuerySolutionMap;

import thewebsemantic.binding.RdfBean;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.RequestTarget;

/**
 * 
 * Data Access Object for synchronous requests.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class RequestDAO extends AbstractDO {

  /**
   * No other instances other then in {@link DAO}.
   */
  protected RequestDAO() {

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
      case MEASUREMENT:
        ret = super.query(Measurement.class, query, start, max);
        break;
      default:
        ret = null;
        break;
    }
    return ret;
  }

  /**
   * Make a synchronous request count to the KB.
   * 
   * @param target
   *          The request target
   * @param query
   *          The request query
   * @param bindings
   *          Initial bindings for the variable
   * @return The number of items of the list of resources, of the type defined
   *         in the target, filtered by the above constraints
   */
  @SuppressWarnings("unchecked")
  public int count(final RequestTarget target, final String query, final QuerySolutionMap bindings) {
    int ret;
    switch (target) {
      case ENTITY_TYPE:
        ret = super.count(EntityType.class, query, bindings);
        break;
      case PROPERTY:
        ret = super.count(Property.class, query, bindings);
        break;
      case ENTITY:
        ret = super.count(Entity.class, query, bindings);
        break;
      case PROPERTY_VALUE:
        ret = super.count(PropertyValue.class, query, bindings);
        break;
      case MEASUREMENT:
        ret = super.count(Measurement.class, query, bindings);
        break;
      default:
        ret = -1;
        break;
    }
    return ret;
  }

  public int count(final RequestTarget target, final String query) {
    return count(target, query, new QuerySolutionMap());
  }
}
