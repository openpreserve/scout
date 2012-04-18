package eu.scape_project.watch.utils.exceptions;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;

/**
 * 
 * Exception thrown when value Java Classes are not compatible with
 * {@link DataType}. For instance, the value of a {@link PropertyValue} related
 * to a {@link Property} with the data type {@link DataType#STRING} should be of
 * the class {@link String}.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class InvalidJavaClassForDataTypeException extends Exception {

  /**
   * Generated UID.
   */
  private static final long serialVersionUID = 8581006196395901157L;

  /**
   * The value that raised the exception.
   */
  private final Object value;

  /**
   * The data type of the {@link Property} related with the
   * {@link PropertyValue}.
   */
  private final DataType datatype;

  /**
   * Create a new exception when Java Classes are not compatible with
   * {@link DataType}.
   * 
   * @param value
   *          The value that raised the exception.
   * @param datatype
   *          The data type of the {@link Property} related with the
   *          {@link PropertyValue}.
   */
  public InvalidJavaClassForDataTypeException(final Object value, final DataType datatype) {
    super(value + " of class " + value.getClass().getName() + " is incompatible with data type " + datatype);

    this.value = value;
    this.datatype = datatype;
  }

  public Object getValue() {
    return value;
  }

  public DataType getDatatype() {
    return datatype;
  }

}
