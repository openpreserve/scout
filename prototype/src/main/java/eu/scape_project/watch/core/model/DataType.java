package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "datatype")
@XmlEnum
public enum DataType {

  INTEGER, TEXT, ORDINAL, URI;

}
