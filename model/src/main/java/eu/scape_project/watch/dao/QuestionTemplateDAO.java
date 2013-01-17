package eu.scape_project.watch.dao;

import java.util.List;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.QueryBinding;
import eu.scape_project.watch.domain.QuestionTemplate;
import eu.scape_project.watch.domain.QuestionTemplateParameter;

/**
 * {@link QuestionTemplate} data access object.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class QuestionTemplateDAO extends AbstractDO<QuestionTemplate> {

  /**
   * No other instances can exist as this is a singleton.
   */
  protected QuestionTemplateDAO() {

  }

  /**
   * Find {@link QuestionTemplate} by id.
   * 
   * @param questionTemplateId
   *          the id
   * @return the {@link QuestionTemplate} or <code>null</code> if not found
   */
  public QuestionTemplate findById(final String questionTemplateId) {
    return super.findById(questionTemplateId, QuestionTemplate.class);
  }

  /**
   * Query for {@link QuestionTemplate}.
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
  public List<QuestionTemplate> query(final String bindings, final int start, final int max) {
    return super.query(QuestionTemplate.class, bindings, start, max);
  }

  /**
   * List all question templates.
   * 
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link QuestionTemplate} filtered by the above
   *         constraints
   */
  public List<QuestionTemplate> list(final int start, final int max) {
    return this.query("", start, max);
  }

  /**
   * Count the results of a query for {@link QuestionTemplate}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(QuestionTemplate.class, bindings);
  }

  /**
   * Deeply save question template, including all parameters, and fire update
   * event.
   * 
   * @param template
   *          The question template to save
   * @return The saved question template.
   */
  public QuestionTemplate save(final QuestionTemplate template) {
    // save parameters
    for (QuestionTemplateParameter p : template.getParameters()) {
      p.save();
    }

    final QuestionTemplate commited = template.save();
    DAO.fireOnUpdated(commited);
    return commited;
  }

  /**
   * Cascading delete template, including all parameters, and fire remove event.
   * 
   * @param template
   *          The question template to delete
   * @return The deleted question template.
   */
  @Override
  public QuestionTemplate delete(final QuestionTemplate template) {
    // delete triggers
    for (QuestionTemplateParameter p : template.getParameters()) {
      p.delete();
    }

    template.delete();
    DAO.fireOnRemoved(template);
    return template;
  }

  public QuerySolutionMap parseBindings(List<QueryBinding> bindings) {
    final QuerySolutionMap ret = new QuerySolutionMap();

    for (final QueryBinding binding : bindings) {
      // TODO get query parameter
      // Create RDF node or literal and add it to ret.
      
      // ret.add(name, node)
    }

    return ret;

  }

}
