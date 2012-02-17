package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.model.AsyncRequest;

/**
 * {@link AsyncRequest} data access object.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class AsyncRequestDAO extends AbstractDAO {

  /**
   * Logger.
   */
  // private static final Logger LOG =
  // LoggerFactory.getLogger(AsyncRequestDAO.class);

  /**
   * Find {@link AsyncRequest} by id.
   * 
   * @param requestId
   *          the request ud
   * @return the {@link AsyncRequest} or <code>null</code> if not found
   */
  public static AsyncRequest findById(final String requestId) {
    return findById(requestId, AsyncRequest.class);
  }

}
