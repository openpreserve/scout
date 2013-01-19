package eu.scape_project.watch.dao;

import java.util.List;

import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.QueryBinding;
import eu.scape_project.watch.domain.QuestionTemplate;
import eu.scape_project.watch.domain.QuestionTemplateParameter;
import eu.scape_project.watch.domain.QuestionTemplateParameter.ParameterType;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.utils.KBUtils;

/**
 * {@link QuestionTemplate} data access object.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class QuestionTemplateParameterDAO extends AbstractDO<QuestionTemplateParameter> {

  /**
   * No other instances can exist as this is a singleton.
   */
  protected QuestionTemplateParameterDAO() {

  }

  /**
   * Find {@link QuestionTemplateParameter} by id.
   * 
   * @param questionTemplateParameterId
   *          the id
   * @return the {@link QuestionTemplateParameter} or <code>null</code> if not
   *         found
   */
  public QuestionTemplateParameter findById(final String questionTemplateParameterId) {
    return super.findById(questionTemplateParameterId, QuestionTemplateParameter.class);
  }

  /**
   * Query for {@link QuestionTemplateParameter}.
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
  public List<QuestionTemplateParameter> query(final String bindings, final int start, final int max) {
    return super.query(QuestionTemplateParameter.class, bindings, start, max);
  }

  /**
   * List all question template parameters.
   * 
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link QuestionTemplate} filtered by the above
   *         constraints
   */
  public List<QuestionTemplateParameter> list(final int start, final int max) {
    return this.query("", start, max);
  }

  /**
   * Count the results of a query for {@link QuestionTemplateParameter}.
   * 
   * @param bindings
   *          The query bindings, see {@link AbstractDO#count(Class, String)}
   * @return The number of results expected for the query
   */
  public int count(final String bindings) {
    return super.count(QuestionTemplateParameter.class, bindings);
  }

  public QuerySolutionMap parseBindings(List<QueryBinding> bindings) {
    final QuerySolutionMap ret = new QuerySolutionMap();

    for (final QueryBinding binding : bindings) {
      // get query parameter
      final QuestionTemplateParameter parameter = findById(binding.getKey());

      if (parameter != null) {
        RDFNode node = null;
        // Create RDF node or literal and add it
        if (parameter.getParameterType().equals(ParameterType.NODE)) {
          final RequestTarget target = parameter.getNodeFilterTarget();
          node = KBUtils.parseResource(binding.getValue(), target.getTargetClass());
        } else if (parameter.getParameterType().equals(ParameterType.LITERAL)) {
          final DataType dataType = parameter.getLiteralFilterDatatype();
          node = KBUtils.parseLiteral(binding.getValue(), dataType);
        }

        if (node != null) {
          ret.add(parameter.getSparqlVariable(), node);
        }

      }
    }

    return ret;

  }
}
