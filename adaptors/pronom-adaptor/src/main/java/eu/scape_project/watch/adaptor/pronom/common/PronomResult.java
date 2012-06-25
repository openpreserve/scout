package eu.scape_project.watch.adaptor.pronom.common;

import java.util.List;

import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.interfaces.ResultInterface;

/**
 * A helper wrapper for the result.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PronomResult implements ResultInterface {

  /**
   * The result property values.
   */
  private List<PropertyValue> result;

  /**
   * Constructs the result
   * 
   * @param result
   */
  public PronomResult(final List<PropertyValue> result) {
    this.result = result;
  }

  @Override
  public List<PropertyValue> getPropertyValues() {
    return this.result;
  }

}
