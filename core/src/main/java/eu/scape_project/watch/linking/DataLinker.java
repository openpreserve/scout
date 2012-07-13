package eu.scape_project.watch.linking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * Component that allows the creation of new links between source adaptors
 * created information on the knowledge base. The linker listens to the creation
 * of entities and property values and uses specialized rules to create the
 * links.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class DataLinker {

  private static final Logger LOG = LoggerFactory.getLogger(DataLinker.class);

  /**
   * Entity listener to call appropriate rules.
   */
  private class DataLinkerEntityListener implements DOListener<Entity> {

    @Override
    public void onUpdated(final Entity entity) {
      onEntityChanged(entity);
    }

    @Override
    public void onRemoved(final Entity entity) {
      onEntityChanged(entity);
    }

  }

  /**
   * Property value listener to call appropriate rules.
   */
  private class DataLinkerPropertyValueListener implements DOListener<PropertyValue> {

    @Override
    public void onUpdated(final PropertyValue value) {
      onPropertyValueChanged(value);
    }

    @Override
    public void onRemoved(final PropertyValue value) {
      onPropertyValueChanged(value);
    }

  }

  /**
   * The list of rules that create links.
   */
  private final List<LinkRule> rules;

  /**
   * Helper map to quickly find the rules that observe an entity type.
   */
  private final Map<EntityType, Set<LinkRule>> typeObservers;

  /**
   * Helper map to quickly find the rules that observe a property.
   */
  private final Map<Property, Set<LinkRule>> propertyObservers;

  /**
   * Create a new data linker.
   */
  public DataLinker() {
    rules = new ArrayList<LinkRule>();
    typeObservers = new HashMap<EntityType, Set<LinkRule>>();
    propertyObservers = new HashMap<Property, Set<LinkRule>>();

    init();
  }

  /**
   * Initialize listeners.
   */
  private void init() {
    DAO.addDOListener(Entity.class, new DataLinkerEntityListener());
    DAO.addDOListener(PropertyValue.class, new DataLinkerPropertyValueListener());
  }

  /**
   * Add a new rule.
   * 
   * @param rule
   *          The rule to add.
   */
  public void addLinkRule(final LinkRule rule) {
    rules.add(rule);

    // update entity type observers cash
    for (final EntityType type : rule.getEntityTypesObserved()) {
      Set<LinkRule> ruleList = typeObservers.get(type);

      if (ruleList == null) {
        ruleList = new HashSet<LinkRule>();
        typeObservers.put(type, ruleList);
      }

      ruleList.add(rule);
    }

    // update property observers cash
    for (final Property property : rule.getPropertiesObserved()) {
      Set<LinkRule> ruleList = propertyObservers.get(property);

      if (ruleList == null) {
        ruleList = new HashSet<LinkRule>();
        propertyObservers.put(property, ruleList);
      }

      ruleList.add(rule);
    }
  }

  /**
   * Remove an existing rule.
   * 
   * @param rule
   *          The rule to remove.
   */
  public void removeLinkRule(final LinkRule rule) {
    rules.remove(rule);

    // update entity type observers cash
    for (final EntityType type : rule.getEntityTypesObserved()) {
      final Set<LinkRule> ruleList = typeObservers.get(type);

      if (ruleList != null) {
        ruleList.remove(rule);

        if (ruleList.isEmpty()) {
          typeObservers.remove(type);
        }
      }
    }

    // update property observers cash
    for (final Property property : rule.getPropertiesObserved()) {
      final Set<LinkRule> ruleList = propertyObservers.get(property);

      if (ruleList != null) {
        ruleList.remove(rule);

        if (ruleList.isEmpty()) {
          propertyObservers.remove(property);
        }
      }
    }
  }

  /**
   * Call rules affected by entities of this type.
   * 
   * @param entity
   *          The updated or removed entity that must be processed by the
   *          affected rules.
   */
  private void onEntityChanged(final Entity entity) {
    final EntityType type = entity.getEntityType();

    if (type != null) {
      final Set<LinkRule> typeRules = typeObservers.get(type);

      if (typeRules != null) {
        for (final LinkRule rule : typeRules) {
          rule.findAndCreateLinks(entity);
        }
      }
    }
  }

  /**
   * Call rules affected by values of this property.
   * 
   * @param value
   *          The updated or removed property value that must be processed by
   *          the affected rules.
   */
  private void onPropertyValueChanged(final PropertyValue value) {
    final Property property = value.getProperty();

    if (property != null) {
      final Set<LinkRule> propertyRules = propertyObservers.get(property);

      if (propertyRules != null) {
        for (final LinkRule rule : propertyRules) {
          rule.findAndCreateLinks(value);
        }
      }
    }
  }

}
