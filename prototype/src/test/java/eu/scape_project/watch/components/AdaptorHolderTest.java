package eu.scape_project.watch.components;

import org.junit.Assert;
import org.junit.Test;

import eu.scape_project.watch.components.elements.Task;

public class AdaptorHolderTest {

  @Test
  public void testAdaptorHolder() {
    AdaptorHolder tAdaptorHolder = new AdaptorHolder();
    Assert.assertTrue(tAdaptorHolder.getNextTime() == Long.MAX_VALUE);
  }

  @Test
  public void testAddTask() {
    // AdaptorHolder tAdaptorHolder = new AdaptorHolder();
    // tAdaptorHolder.addTask(new Task(), 0, 100);
    // Assert.assertTrue(tAdaptorHolder.getNextTime()==Long.MAX_VALUE);
  }
}
