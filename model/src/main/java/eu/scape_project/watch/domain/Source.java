package eu.scape_project.watch.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.utils.KBUtils;
import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;


/**
 * A Source represents certain aspects of the world for which there exists a
 * known way of investigating certain properties that represent these aspects.
 * Sources can be internal or external, i.e. they can be part of the
 * organization responsible for preservation or part of the outside world.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.SOURCE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Source extends RdfBean<Source> {

  /**
   * Create a new empty source.
   */
  public Source() {
    super();
  }

  /**
   * Create a new source.
   * 
   * @param name
   *          A unique name
   * @param desc
   *          A description
   */
  public Source(final String name, final String desc) {
    this.name = name;
    this.description = desc;
  }

  /**
   * Source unique name.
   */
  @Id
  @XmlElement
  private String name;

  /**
   * Description of the Source.
   */
  @XmlElement
  private String description;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
    final Source other = (Source) obj;
    if (this.description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!this.description.equals(other.description)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

}
