package eu.scape_project.watch.utils.exceptions;

import eu.scape_project.watch.domain.DataType;

/**
 * Exception thrown when {@link DataType} is not supported by the method.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class UnsupportedDataTypeException extends Exception {

  /**
   * Generated UID.
   */
  private static final long serialVersionUID = 2058868659582729647L;

  /**
   * The {@link DataType} that raised the exception.
   */
  private final DataType datatype;

  /**
   * New unsupported data type exception.
   * 
   * @param datatype
   *          The {@link DataType} that raised the exception.
   */
  public UnsupportedDataTypeException(final DataType datatype) {
    super("" + datatype);

    this.datatype = datatype;
  }

  public DataType getDatatype() {
    return datatype;
  }

}
