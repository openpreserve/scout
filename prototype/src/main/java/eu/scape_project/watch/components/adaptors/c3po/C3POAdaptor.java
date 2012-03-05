package eu.scape_project.watch.components.adaptors.c3po;

import java.io.InputStream;

import eu.scape_project.watch.components.Adaptor;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A watch conforming adaptor for a collection profile source called c3po.
 * 
 * @author peter
 * @version 0.1
 */
public class C3POAdaptor extends Adaptor {

  private static final Logger LOG = LoggerFactory.getLogger(C3POAdaptor.class);

  private static final String[] SUPPORTED_PROPERTIES = {"name", "count"};

  public C3POAdaptor() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkForTask(final Task t) {
    final EntityType type = t.getEntity().getEntityType();

    if (type == null) {
      LOG.warn("EntityType of the provided entity is null. Cannot determine if supported.");
      return false;
    }

    // TODO this should be a constant or similar.
    if (!type.getName().equals("COLLECTION_PROFILE")) {
      LOG.warn("EntityType {} is not supported", type.getName());
      return false;
    }

    for (String p : SUPPORTED_PROPERTIES) {
      if (t.getProperty().getName().equalsIgnoreCase(p)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Fetches dummy data from a profile output. However the no real calls to the
   * REST API of c3po are done yet.
   */
  @Override
  protected void fetchData() {

    // TODO fetch get collection name from

    for (Task t : this.tasks) {

      InputStream is = C3POAdaptor.class.getClassLoader().getResourceAsStream("dummy_profile.xml");
      C3POProfileReader reader = new C3POProfileReader(is);
      
      String name = reader.getCollectionName();
      
      Entity e = new Entity(t.getEntity().getEntityType(), name);
      
      Property p = t.getProperty();
      PropertyValue pv = null;
      if (p.getName().equalsIgnoreCase("name")) {
        pv = new PropertyValue(e, p, name);
      } else if (p.getName().equalsIgnoreCase("count")) {
        String count = reader.getObjectsCount();
        pv = new PropertyValue(e, p, count);
      }
      
    }

  }

}
