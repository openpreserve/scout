package eu.scape_project.watch.linking;

import java.util.List;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * 
 * Interface to define how new links between entities and property values are
 * created.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public interface LinkRule {

  /**
   * List of entity types that this link rules observes. This means that the
   * method {@link #findAndCreateLinks(Entity)} will be called whenever an
   * entity of a type included and this list is created or updated.
   * 
   * @return The list of entity types for which to listen the creation or update
   *         of entities.
   */
  List<EntityType> getEntityTypesObserved();

  /**
   * List of properties that this link rules observes. This means that the
   * method {@link #findAndCreateLinks(PropertyValue)} will be called whenever
   * an value of that property included and this list is created or updated.
   * 
   * @return The list of properties for which to listen the creation or update
   *         of values.
   */
  List<Property> getPropertiesObserved();

  /**
   * Query the knowledge base to find and new links related with the given
   * entity and update the knowledge base accordingly.
   * 
   * @param newEntity
   *          The created or updated entity for which new links should be found
   *          and created.
   */
  void findAndCreateLinks(Entity newEntity);

  /**
   * Query the knowledge base to find and new links related with the given
   * property value and update the knowledge base accordingly.
   * 
   * @param newPropertyValue
   *          The created or updated property value for which new links should
   *          be found and created.
   */

  void findAndCreateLinks(PropertyValue newPropertyValue);

}
