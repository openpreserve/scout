package eu.scape_project.watch.model;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A {@link PropertyValue} test class.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PropertyValueTest {

  /*
   * Proof that the implementation of PropertyValue.getValue(Class<T> clazz) is
   * faulty. The problem lies in the fact that the check in the getValue()
   * method only checks if the list objects (stringListValue and
   * stringDictionaryValue) are not null, which they are not, as they are
   * initialized at object creation. We should probably check if they are not
   * null AND if they are not empty.
   * 
   * The test will fail.
   */
  /**
   * Tests whether the retrieval of the raw values from a {@link PropertyValue}
   * class returns a correct type of values if the datatype of the property is
   * {@link DataType#STRING_DICTIONARY}.
   * 
   * @throws Exception
   *           if something goes wrong.
   */
  @Test
  public void shouldGetDictionaryItemList() throws Exception {
    // mock unnecessary classes.
    final Entity e1 = mock(Entity.class);
    final Property p1 = mock(Property.class);

    // make sure that the datatype precondition is met by the mocks.
    when(p1.getDatatype()).thenReturn(DataType.STRING_DICTIONARY);

    // create some non empty list with dictionary items.
    final List<DictionaryItem> values = new ArrayList<DictionaryItem>();
    values.add(new DictionaryItem("key", "value"));

    // instantiate a new property value with the list as value.
    final PropertyValue value = new PropertyValue(e1, p1, values);

    // retrieve the value and check if it contains the inserted
    // DictionaryItem
    final List<DictionaryItem> retrievedList = value.getValue(List.class);

    Assert.assertNotNull(retrievedList);
    Assert.assertEquals(values.size(), retrievedList.size());
    Assert.assertEquals(1, retrievedList.size());
  }
}
