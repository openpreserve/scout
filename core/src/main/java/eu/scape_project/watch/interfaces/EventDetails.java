package eu.scape_project.watch.interfaces;

/**
 * 
 * @author Kresimir Duretec <duretec@ifs.tuwien.ac.at>
 *
 */
public interface EventDetails {

  /**
   * Set the successfulness of an event.
   * @param suc
   */
  void setSuccessful(boolean suc);
  
  /**
   * Returns true if the action was successfully executed otherwise false 
   * @return
   */
  boolean isSucessful();
  
  /**
   * Add message to EventDetails
   * @param msg - message to be added
   */
  void addMessage(String msg);
  
  /**
   *  Returns a message describing the event 
   * @return 
   */
  String getMessage();
  
  /**
   * The reason for an event to occur. 
   * @param reason
   */
  void setReason(String reason);
  
  
  /**
   * Returns the reason for an event to occur.
   * @return reason
   */
  String getReason();
  
}
