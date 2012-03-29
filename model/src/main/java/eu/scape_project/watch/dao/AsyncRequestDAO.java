package eu.scape_project.watch.dao;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Trigger;

import java.util.Collection;
import java.util.List;

/**
 * {@link AsyncRequest} data access object.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class AsyncRequestDAO extends AbstractDO<AsyncRequest> {

  /**
   * Singleton instance.
   */
  private static AsyncRequestDAO instance = null;

  /**
   * Get singleton instance.
   * 
   * @return The singleton instance
   */
  public static AsyncRequestDAO getInstance() {
    if (instance == null) {
      instance = new AsyncRequestDAO();
    }
    return instance;
  }

  /**
   * No other instances can exist as this is a singleton.
   */
  private AsyncRequestDAO() {

  }

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
  public AsyncRequest findById(final String requestId) {
    return super.findById(requestId, AsyncRequest.class);
  }

  /**
   * Query for {@link AsyncRequest}.
   * 
   * @see #query(Class, String, int, int)
   * 
   * @param bindings
   *          The query bindings, see
   *          {@link AbstractDO#query(Class, String, int, int)}
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link AsyncRequest} filtered by the above constraints
   */
  public List<AsyncRequest> query(final String bindings, final int start, final int max) {
    return super.query(AsyncRequest.class, bindings, start, max);
  }

  /**
   * List all async requests.
   * 
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link AsyncRequest} filtered by the above constraints
   */
  public Collection<AsyncRequest> list(final int start, final int max) {
    return this.query("", start, max);
  }

  /**
   * Count the results of a query for {@link AsyncRequest}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(AsyncRequest.class, bindings);
  }

  /**
   * Deeply save request, including all triggers, and fire update event.
   * 
   * @param request
   *          The async request to save
   * @return The saved async request.
   */
  @Override
  public AsyncRequest save(final AsyncRequest request) {
    // save triggers
    for (Trigger t : request.getTriggers()) {
      // save plan
      final Plan plan = t.getPlan();
      if (plan != null) {
        plan.save();
      }

      // save question
      t.getQuestion().save();

      // save notifications
      for (Notification n : t.getNotifications()) {
        n.save();
      }

      t.save();
    }

    request.save();
    return request;
  }

  /**
   * Cascading delete request, including all triggers, and fire remove event.
   * 
   * @param request
   *          The async request to delete
   * @return The deleted async request.
   */
  @Override
  public AsyncRequest delete(final AsyncRequest request) {
    // delete triggers
    for (Trigger t : request.getTriggers()) {
      // delete plan
      if (t.getPlan() != null) {
        t.getPlan().delete();
      }

      // delete question
      t.getQuestion().delete();

      // delete notifications
      for (Notification n : t.getNotifications()) {
        n.delete();
      }

      t.delete();
    }

    request.delete();
    fireOnUpdated(request);
    return request;
  }

}
