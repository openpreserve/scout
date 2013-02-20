package eu.scape_project.watch.domain;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.utils.KBUtils;

/**
 * Query Binding allows for sparql initial bindings, or {@link QuerySolutionMap}
 * , to be defined for a {@link Question}. These bindings are specialy used with
 * using {@link QuestionTemplate} to create the {@link Question}.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.QUERY_BINDING)
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryBinding extends RdfBean<QueryBinding> {

  /**
   * Parse an string encoded query binding.
   * 
   * @param stringEncoded
   *          The query binding in encoded form.
   * @return The query binding or <code>null</code> if no separator found.
   */
  public static QueryBinding valueOf(final String stringEncoded) {
    final int indexOfSeparator = stringEncoded.indexOf('|');

    QueryBinding ret = null;

    if (indexOfSeparator >= 0) {
      final String key = stringEncoded.substring(0, indexOfSeparator);
      final String value = stringEncoded.substring(indexOfSeparator + 1);
      ret = new QueryBinding(key, value);
    }
    return ret;
  }

  /**
   * The unique identifier of the query binding.
   */
  @Id
  @XmlElement(required = true)
  private String id;
  
  private String questionTemplateParameterId;
  private String value;

  public QueryBinding() {
    this.id = UUID.randomUUID().toString();
  }

  public QueryBinding(String questionTemplateParameterId, String value) {
    super();
    this.id = UUID.randomUUID().toString();
    this.questionTemplateParameterId = questionTemplateParameterId;
    this.value = value;
  }

  public String getQuestionTemplateParameterId() {
    return questionTemplateParameterId;
  }

  public void setQuestionTemplateParameterId(String questionTemplateParameterId) {
    this.questionTemplateParameterId = questionTemplateParameterId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("QueryBinding [questiontemplateParameterId=%s, value=%s]", questionTemplateParameterId, value);
  }

}
