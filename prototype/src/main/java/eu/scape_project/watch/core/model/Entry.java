package eu.scape_project.watch.core.model;

import eu.scape_project.watch.core.KBUtils;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

/**
 * 
 * A key-value pair entry holder.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.ENTRY)
@XmlAccessorType(XmlAccessType.FIELD)
public class Entry {

  /**
   * The unique id that identifies this entry.
   */
  @Id
  @XmlTransient
  @JsonIgnore
  private final String id;

  /**
   * Entry key.
   */
  @XmlElement
  private String key;

  /**
   * Entry value.
   */
  @XmlElement
  private String value;

  /**
   * Create a new empty entry.
   */
  public Entry() {
    super();
    this.id = UUID.randomUUID().toString();
  }

  /**
   * Create a new entry.
   * 
   * @param key
   *          the entry key
   * @param value
   *          the entry value
   */
  public Entry(final String key, final String value) {
    this();
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public String getId() {
    return this.id;
  }

}
