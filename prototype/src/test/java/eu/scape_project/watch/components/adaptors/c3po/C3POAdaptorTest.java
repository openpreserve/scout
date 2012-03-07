package eu.scape_project.watch.components.adaptors.c3po;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;
import eu.scape_project.watch.components.elements.Result;
import eu.scape_project.watch.components.elements.Task;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyDataStructure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class C3POAdaptorTest {

  private static final String COLLECTION_PROFILE = "COLLECTION_PROFILE";
  private C3POAdaptor adaptor;

  @Before
  public void setup() {
    this.adaptor = new C3POAdaptor();
  }

  @Test
  public void shouldTestC3POAdaptorConfigurationCheck() throws Exception {
    Task task = this.getMockTask(true);
    Assert.assertTrue(this.adaptor.checkForTask(task));
    
    task = this.getMockTask(false);
    Assert.assertFalse(this.adaptor.checkForTask(task));
    
    Assert.assertFalse(this.adaptor.checkForTask(null));
  }

  @Test
  public void shouldTestC3POAdaptorObjectsCountRetrieval() throws Exception {

    final EntityType et = new EntityType(COLLECTION_PROFILE, "This is a collection profile");
    final Entity e = new Entity(et, "coll-0-test");
    final Property p = new Property(et, C3POConstants.CP_OBJECTS_COUNT,
      "Retrieves the count of elements in a collection profile");
    final Task t = new Task(e, p);

    final boolean supported = adaptor.checkForTask(t);
    Assert.assertTrue(supported);

    adaptor.addTask(t);
    adaptor.fetchData();

    final List<Result> results = adaptor.getResults();
    Assert.assertEquals(1, results.size());

    final Result result = results.get(0);
    Assert.assertNotNull(result.getPropertyValue());
    Assert.assertNotSame("", result.getPropertyValue().getValue());
    Assert.assertNotSame(C3POProfileReader.MISSING_VALUE, result.getPropertyValue().getValue());
    final int count = Integer.parseInt(result.getPropertyValue().getValue());
    Assert.assertTrue(count >= 0);

  }
  
  @Test
  public void shouldTestFormatDistributionTask() throws Exception {
    final Task task = this.getMockTask(true);
    Mockito.when(task.getProperty().getName()).thenReturn(C3POConstants.CP_FORMAT_DISTRIBUTION);
    Mockito.when(task.getProperty().getStructure()).thenReturn(PropertyDataStructure.DICTIONARY);
    
    this.adaptor.addTask(task);
    this.adaptor.fetchData();
    
    final List<Result> results = this.adaptor.getResults();
    Assert.assertEquals(1, results.size());
    
    final Result result = results.get(0);
    Assert.assertNotNull(result.getPropertyValue());
    Assert.assertFalse(result.getPropertyValue().getValues().isEmpty());
  }
  
  private Task getMockTask(final boolean valid) {
    final EntityType et = Mockito.mock(EntityType.class);
    final Entity e = Mockito.mock(Entity.class);
    final Property p = Mockito.mock(Property.class);
    final Task task = Mockito.mock(Task.class);

    
    Mockito.when(et.getName()).thenReturn("whatever");
    Mockito.when(p.getName()).thenReturn("false");
    Mockito.when(e.getEntityType()).thenReturn(et);
    Mockito.when(p.getType()).thenReturn(et);
    Mockito.when(task.getEntity()).thenReturn(e);
    Mockito.when(task.getProperty()).thenReturn(p);
   
    if (valid) {
      Mockito.when(et.getName()).thenReturn(COLLECTION_PROFILE);
      Mockito.when(p.getName()).thenReturn(C3POConstants.CP_OBJECTS_COUNT);
      Mockito.when(e.getName()).thenReturn("coll-0-test");
    }
    
    return task;
  }
}
