package eu.scape_project.watch.merging;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.SourceAdaptor;

/**
 * Default rule to merge entities and property values into the knowledge base.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class DefaultMergeRule implements MergeRule {

  @Override
  public void mergeEntity(final Entity entity) {
    DAO.ENTITY.save(entity);
  }

  @Override
  public void mergePropertyValue(final SourceAdaptor adaptor, final PropertyValue propertyValue) {
    DAO.PROPERTY_VALUE.save(adaptor, propertyValue);
  }

}
