package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import eu.scape_project.watch.core.KBUtils;

/**
 * Defines the data structure of the property, e.g. single value or a list of
 * values. Not to be mistaken with {@link DataType}.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
@XmlType(name = KBUtils.DATA_STRUCTURE_TYPE)
@XmlEnum
public enum PropertyDataStructure {

  /**
   * Denotes a single value property.
   */
  SINGLE,

  /**
   * Denotes a property, where each value has a list of elements.
   */
  LIST,

  /**
   * Denotes a property, where each value has a dictionary (a.k.a Map) of
   * elements.
   */
  DICTIONARY
}
