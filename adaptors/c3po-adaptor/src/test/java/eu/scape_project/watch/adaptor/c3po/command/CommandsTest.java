package eu.scape_project.watch.adaptor.c3po.command;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.scape_project.watch.adaptor.c3po.common.C3POProfileReader;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

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
   * 
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void shouldTestSizeCommand() throws UnsupportedDataTypeException {
    this.cmd = new CollectionSizeCommand();
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getCollectionSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue(String.class));

  }

  /**
   * Tests the {@link ObjectsAvgSizeCommand}.
   * 
   * @throws UnsupportedDataTypeException
   * 
   */
  @Test
  public void shouldTestAvgSizeCommand() throws UnsupportedDataTypeException {
    this.cmd = new ObjectsAvgSizeCommand();
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsAvgSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link ObjectsMinSizeCommand}.
   * 
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void shouldTestMinSizeCommand() throws UnsupportedDataTypeException {
    this.cmd = new ObjectsMinSizeCommand();
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsMinSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link ObjectsMaxSizeCommand}.
   * 
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void shouldTestMaxSizeCommand() throws UnsupportedDataTypeException {
    this.cmd = new ObjectsMaxSizeCommand();
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsMaxSize();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link ObjectsCountCommand}.
   * 
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void shouldTestCountCommand() throws UnsupportedDataTypeException {
    this.cmd = new ObjectsCountCommand();
    this.cmd.setReader(this.reader);
    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getObjectsCount();

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertEquals(VALUE, pv.getValue());
  }

  /**
   * Tests the {@link DistributionCommand}.
   * 
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void shouldTestDistributionCommand() throws UnsupportedDataTypeException {
    final String name = FORMAT;

    this.cmd = new DistributionCommand(name);
    this.cmd.setReader(this.reader);

    final PropertyValue pv = this.cmd.execute();

    verify(this.reader).getDistribution(name);

    assertNotNull(pv);
    assertNotNull(pv.getProperty());
    assertNotNull(pv.getValue());
    assertFalse(pv.getValue(List.class).isEmpty());

  }
}
