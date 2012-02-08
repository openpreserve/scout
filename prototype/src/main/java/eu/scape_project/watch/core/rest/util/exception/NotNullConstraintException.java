package eu.scape_project.watch.core.rest.util.exception;

public class NotNullConstraintException extends RuntimeException {

  private static final long serialVersionUID = 8049303862454909020L;
  
  public NotNullConstraintException() {
    super();
  }
  
  public NotNullConstraintException(String message) {
    super(message);
  }
  
  public NotNullConstraintException(Throwable cause) {
    super(cause);
  }
  
  public NotNullConstraintException(String message, Throwable cause) {
    super(message, cause);
  }

}
