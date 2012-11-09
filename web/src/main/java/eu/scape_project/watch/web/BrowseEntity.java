package eu.scape_project.watch.web;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/entity/([^/]+)")
@TemplateSource("browseEntity")
public class BrowseEntity extends TemplateContext {

  public List<Property> getProperties() {
    return DAO.PROPERTY.listWithType(getEntity().getType().getName(), 0, 100);
  }

  public List<PropertyValue> getValues() {

    // TODO optimize
    final Entity entity = getEntity();
    final List<Property> properties = DAO.PROPERTY.listWithType(entity.getType().getId(), 0, 1000);

    final List<PropertyValue> ret = new ArrayList<PropertyValue>();

    for (final Property property : properties) {
      final PropertyValue pv = DAO.PROPERTY_VALUE.find(entity.getId(), property.getId());
      ret.add(pv);
    }

    // return DAO.PROPERTY_VALUE.listWithEntity(getEntity(), 0, 100);
    return ret;
  }

  @Inject
  private Matcher m;

  public String getId() {
    return m.group(1);
  }

  public Entity getEntity() {
    return DAO.ENTITY.findById(getId());
  }
}
