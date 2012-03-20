package eu.scape_project.watch.domain;

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
   * A numeric value.
   */
  NUMBER,
  /**
   * A textual value.
   */
  TEXT,
  /**
   * A list of possible values.
   * TODO define the possible values
   */
  ORDINAL,
  /**
   * A link to an external resource.
   */
  URI;

}
