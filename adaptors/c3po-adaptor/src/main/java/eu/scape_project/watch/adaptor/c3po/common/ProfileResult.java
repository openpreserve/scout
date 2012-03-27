package eu.scape_project.watch.adaptor.c3po.common;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.interfaces.ResultInterface;

/**
 * A simple profile result object.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ProfileResult implements ResultInterface {

  /**
   * The list of property values that were found out.
   */
  private List<PropertyValue> result;

  /**
   * The default constructor initializes the result list.
   */
  public ProfileResult() {
    this.result = new ArrayList<PropertyValue>();
  }

  /**
   * Initializes this object and passes the result list.
   * 
   * @param result
   *          the result list to use.
   */
  public ProfileResult(final List<PropertyValue> result) {
    this.result = result;
  }

  /**
   * Adds the value to the result list.
   * 
   * @param v
   *          the value to add.
   */
  public void add(final PropertyValue v) {
    this.result.add(v);
  }

  /**
   * Adds all values of the passed list to the result list if the list is not
   * null.
   * 
   * @param values
   *          the values to add.
   */
  public void add(final List<PropertyValue> values) {
    if (values != null) {
      this.result.addAll(values);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PropertyValue> getPropertyValues() {
    return this.result;
  }

}
