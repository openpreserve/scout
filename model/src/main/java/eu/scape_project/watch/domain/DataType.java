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
  STRING,
  /**
   * A {@link Integer}.
   */
  INTEGER,
  /**
   * A {@link Long}.
   */
  // Jena or Jenabean do not seam to support Long
  // LONG,
  /**
   * A {@link Float}.
   */
  FLOAT,
  /**
   * A {@link Double}.
   */
  DOUBLE,
  /**
   * A link to an external resource by {@link URI}.
   */
  URI,
  /**
   * A {@link Date}.
   */
  DATE,
  /**
   * A {@link List} of {@link String}.
   */
  STRING_LIST,
  /**
   * A map of {@link String}, implemented by a {@link List} of
   * {@link DictionaryItem}.
   */
  STRING_DICTIONARY;

}
