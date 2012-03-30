package eu.scape_project.watch.domain;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.scape_project.watch.utils.KBUtils;

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
    result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Entry other = (Entry) obj;
    if (this.key == null) {
      if (other.key != null) {
        return false;
      }
    } else if (!this.key.equals(other.key)) {
      return false;
    }
    if (this.value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Entry [key=" + key + ", value=" + value + "]";
  }

}
