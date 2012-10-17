package eu.scape_project.watch.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import eu.scape_project.watch.utils.KBUtils;

/**
 * Rendering hints for {@link Property}.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@XmlType(name = KBUtils.RENDERING_HINT)
@XmlEnum
public enum RenderingHint {
  STORAGE_VOLUME;

}
