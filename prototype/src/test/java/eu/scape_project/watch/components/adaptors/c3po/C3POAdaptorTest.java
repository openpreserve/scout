package eu.scape_project.watch.components.adaptors.c3po;

import java.util.List;

import junit.framework.Assert;
import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;

import org.junit.Test;

public class C3POAdaptorTest {

  @Test
  public void shouldTestC3POAdapotor() throws Exception {

    C3POAdaptor adaptor = new C3POAdaptor();
    EntityType et = new EntityType("COLLECTION_PROFILE", "This is a collection profile");
    Entity e = new Entity(et, "Test");
    Property p = new Property(et, C3POConstants.CP_OBJECTS_COUNT,
      "Retrieves the count of elements in a collection profile");
    Task t = new Task(e, p);

    boolean supported = adaptor.checkForTask(t);
    Assert.assertTrue(supported);

    adaptor.addTask(t);
    adaptor.fetchData();

    List<Result> results = adaptor.getResults();
    Assert.assertEquals(1, results.size());

    Result result = results.get(0);
    Assert.assertNotNull(result.getPropertyValue());
    Assert.assertNotSame("", result.getPropertyValue().getValue());
    Assert.assertEquals("502", result.getPropertyValue().getValue());

  }
}
