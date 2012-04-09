package eu.scape_project.watch.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.utils.KBUtils;

import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * 
 * Key-Value pair holder for PropertyValue lists of key-value pairs (i.e.
 * dictionary).
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * @author Petar Petrov
 * 
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.DICTIONARY_ITEM)
@XmlAccessorType(XmlAccessType.FIELD)
public class DictionaryItem extends RdfBean<DictionaryItem> {

  /**
   * The key of the key-value pair.
   */
  @XmlElement
  @JsonProperty
  private String key;

  /**
   * The value of the key-value pair.
   */
  @XmlElement
  @JsonProperty
  private String value;

  /**
   * Create a new empty dictionary item.
   */
  public DictionaryItem() {
    super();
  }

  /**
   * Create a new dictionary item.
   * 
   * @param key
   *          The dictionary item key
   * @param value
   *          The dictionary item value
   */
  public DictionaryItem(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
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
    final DictionaryItem other = (DictionaryItem) obj;
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
    return this.key + " : " + this.value;
  }

}
