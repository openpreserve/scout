package eu.scape_project.watch.components.adaptors.c3po;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import eu.scape_project.watch.components.Adaptor;
import eu.scape_project.watch.components.elements.Result;
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
 * @author Petrov <me@petarpetrov.org>
 * @version 0.1
 */
public class C3POAdaptor extends Adaptor {

  private static final Logger LOG = LoggerFactory.getLogger(C3POAdaptor.class);

  private static final String CP_ENDPOINT = "http://localhost:8080/c3po/api";

  private List<String> supportedProperties;

  public C3POAdaptor() {
    this.supportedProperties = Arrays.asList(C3POConstants.CP_COLLECTION_IDENTIFIER, C3POConstants.CP_OBJECTS_COUNT,
      C3POConstants.CP_FORMAT_MODE, C3POConstants.CP_PUID_MODE);
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

    for (String p : supportedProperties) {
      if (t.getProperty().getName().equals(p)) {
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

    // TODO use original client
    IC3POClient client = new C3PODummyClient();
    List<String> identifiers = client.getCollectionIdentifiers();

    for (Task t : this.getTasks()) {

      String id = t.getEntity().getName();
      if (identifiers.contains(id)) {

        // this should be t.getProperties();
        List<String> properties = this.generatePropertyExpansionList(t.getProperty());
        String uuid = client.submitCollectionProfileJob(id, properties);

        InputStream is = client.pollJobResult(uuid);
        while (is == null) {
          is = client.pollJobResult(uuid);
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            LOG.warn("An error occurred while waiting for c3po result: {}", e.getMessage());
          }
        }

        C3POProfileReader reader = new C3POProfileReader(is);
        String name = reader.getCollectionName();

        Entity e = new Entity(t.getEntity().getEntityType(), name);
        Property p = t.getProperty();
        PropertyValue pv = null;

        if (p.getName().equals(C3POConstants.CP_COLLECTION_IDENTIFIER)) {
          pv = new PropertyValue(e, p, name);
        } else if (p.getName().equals(C3POConstants.CP_OBJECTS_COUNT)) {
          String count = reader.getObjectsCount();
          pv = new PropertyValue(e, p, count);
        }

        Result r = new Result(e, p, pv);
        this.results.add(r);
      }
    }

  }

  private List<String> generatePropertyExpansionList(Property property) {
    LOG.debug("generating parameter for property {}", property.getName());
    return Arrays.asList(property.getName());
  }

}
