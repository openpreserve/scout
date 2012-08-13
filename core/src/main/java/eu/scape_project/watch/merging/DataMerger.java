package eu.scape_project.watch.merging;

import java.util.HashMap;
import java.util.Map;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;

/**
 * Component that allows the merging of source adaptors created information into
 * the knowledge base so data incoherences are treated. The merger allows for
 * entity type or property specialized rules to be used.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class DataMerger {

  /**
   * List of entity type specialized rules.
   */
  private final Map<EntityType, MergeRule> typeSpecializedRules;

  /**
   * List of property specialized rules.
   */
  private final Map<Property, MergeRule> propertySpecializedRules;

  /**
   * Default merge rule.
   */
  private final MergeRule defaultRule;

  /**
   * Create a new Data Merger.
   */
  public DataMerger() {
    typeSpecializedRules = new HashMap<EntityType, MergeRule>();
    propertySpecializedRules = new HashMap<Property, MergeRule>();

    defaultRule = new DefaultMergeRule();
  }

  /**
   * Set an entity type specialized rule.
   * 
   * @param type
   *          The type of entities to be processed by this rule.
   * @param rule
   *          The rule used to merge entities of the given type into the
   *          knowledge base.
   */
  public void setMergeRule(final EntityType type, final MergeRule rule) {
    typeSpecializedRules.put(type, rule);
  }

  /**
   * Set an property specialized rule.
   * 
   * @param property
   *          The related property of the property values to be processed by
   *          this rule.
   * @param rule
   *          The rule used to merge values of the given property into the
   *          knowledge base.
   */
  public void setMergeRule(final Property property, final MergeRule rule) {
    propertySpecializedRules.put(property, rule);
  }

  /**
   * Merge a source adaptor created entity into the knowledge base.
   * 
   * @param entity
   *          The entity to merge.
   */
  public void merge(final Entity entity) {
    final EntityType type = entity.getEntityType();

    final MergeRule mergeRule = typeSpecializedRules.get(type);

    if (mergeRule != null) {
      mergeRule.mergeEntity(entity);
    } else {
      defaultRule.mergeEntity(entity);
    }
  }

  /**
   * Merge a source adaptor created property value into the knowledge base.
   * 
   * @param adaptor
   *          The source adaptor that took the property value measurement.
   * 
   * @param propertyValue
   *          The property value to merge.
   */
  public void merge(final SourceAdaptor adaptor, final PropertyValue propertyValue) {
    final Property property = propertyValue.getProperty();

    final MergeRule mergeRule = propertySpecializedRules.get(property);

    if (mergeRule != null) {
      mergeRule.mergePropertyValue(adaptor, propertyValue);
    } else {
      this.defaultRule.mergePropertyValue(adaptor, propertyValue);
    }

  }
}
