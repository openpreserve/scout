package eu.scape_project.watch.adaptor.c3po.command;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.scape_project.watch.adaptor.c3po.common.C3POProfileReader;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * Tests the commands that read the profile.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class CommandsTest {

  /**
   * A format string for tests.
   */
  private static final String FORMAT = "format";

  /**
   * A dummy value.
   */
  private static final String VALUE = "42";

  /**
   * The object under test.
   */
  private Command cmd;

  /**
   * A dummy property.
   */
  private Property property;

  /**
   * A dummy reader.
   */
  private C3POProfileReader reader;

  /**
   * A dummy map for testing purposes.
   */
  private Map<String, String> values;

  /**
   * Mocks the unnecessary objects.
   */
  @Before
  public void setup() {
    this.property = mock(Property.class);
    this.reader = mock(C3POProfileReader.class);
    this.values = new HashMap<String, String>();
    this.values.put("test", VALUE);

    when(this.reader.getCollectionSize()).thenReturn(VALUE);
    when(this.reader.getCollectionName()).thenReturn("TestCollection");
    when(this.reader.getObjectsAvgSize()).thenReturn(VALUE);
    when(this.reader.getObjectsCount()).thenReturn(VALUE);
    when(this.reader.getObjectsMaxSize()).thenReturn(VALUE);
    when(this.reader.getObjectsMinSize()).thenReturn(VALUE);
    when(this.reader.getDistribution(FORMAT)).thenReturn(this.values);

  }

  /**
   * Tears down the test.
   */
  @After
  public void teardown() {
    this.cmd = null;
  }

  /**
   * Tests the {@link CollectionSizeCommand}.
   */
  @Test
  public void shouldTestSizeCommand() {
    this.cmd = new CollectionSizeCommand(this.property);
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getCollectionSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());

  }

  /**
   * Tests the {@link ObjectsAvgSizeCommand}.
   * 
   */
  @Test
  public void shouldTestAvgSizeCommand() {
    this.cmd = new ObjectsAvgSizeCommand(this.property);
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsAvgSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link ObjectsMinSizeCommand}.
   */
  @Test
  public void shouldTestMinSizeCommand() {
    this.cmd = new ObjectsMinSizeCommand(this.property);
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsMinSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link ObjectsMaxSizeCommand}.
   */
  @Test
  public void shouldTestMaxSizeCommand() {
    this.cmd = new ObjectsMaxSizeCommand(this.property);
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsMaxSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link ObjectsCountCommand}.
   */
  @Test
  public void shouldTestCountCommand() {
    this.cmd = new ObjectsCountCommand(this.property);
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsCount();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link DistributionCommand}.
   */
  @Test
  public void shouldTestDistributionCommand() {
    final String name = FORMAT;

    this.cmd = new DistributionCommand(this.property, name);
    this.cmd.setReader(this.reader);

    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getDistribution(name);

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertNull(pv.getValue());
    assertNotNull(pv.getValues());
    assertFalse(pv.getValues().isEmpty());

  }
}
