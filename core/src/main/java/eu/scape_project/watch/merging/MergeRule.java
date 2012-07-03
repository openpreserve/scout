package eu.scape_project.watch.merging;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.SourceAdaptor;

/**
 * Interface to define how new knowledge is merged into the current state of the
 * knowledge base, without introducing incoherences.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public interface MergeRule {

  /**
   * Merge a source created entity into the knowledge base.
   * 
   * @param entity
   *          The generated entity.
   */
  void mergeEntity(Entity entity);

  /**
   * Merge a source created property value into the knowledge base.
   * 
   * @param adaptor
   *          Information about which was the adaptor that took this
   *          information.
   * @param propertyValue
   *          The gathered information
   */
  void mergePropertyValue(SourceAdaptor adaptor, PropertyValue propertyValue);

}
