package eu.scape_project.watch.domain;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import eu.scape_project.watch.utils.KBUtils;

/**
 * Possible data types of {@link Property}.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@XmlType(name = KBUtils.DATA_TYPE)
@XmlEnum
public enum DataType {
  /**
   * A {@link String}.
   */
  STRING("Plain text"),
  /**
   * A {@link Integer}.
   */
  INTEGER("Integer number"),
  /**
   * A {@link Long}.
   */
  // Jena or Jenabean do not seam to support Long
  // LONG,
  /**
   * A {@link Float}.
   */
  FLOAT("Real number"),
  /**
   * A {@link Double}.
   */
  DOUBLE("Real number (with extra precision)"),
  /**
   * A link to an external resource by {@link URI}.
   */
  URI("Link to external resource"),
  /**
   * A {@link Date}.
   */
  DATE("Date"),
  /**
   * A {@link List} of {@link String}.
   */
  STRING_LIST("List of values in plain text"),
  /**
   * A map of {@link String}, implemented by a {@link List} of
   * {@link DictionaryItem}.
   */
  STRING_DICTIONARY("List of key-value pairs in plain text");
  
  private String description;
  
  private DataType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
  
}
