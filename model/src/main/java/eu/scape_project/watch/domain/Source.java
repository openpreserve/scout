package eu.scape_project.watch.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import eu.scape_project.watch.dao.DAO;
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

    updateId();
  }

  /**
   * The unique id generated as a hash of the name.
   */
  @Id
  @XmlElement
  private String id;

  /**
   * Source unique name.
   */
  @XmlElement
  private String name;

  /**
   * Description of the Source.
   */
  @XmlElement
  private String description;

  /**
   * Update the id based on the name.
   */
  private void updateId() {
    this.id = KBUtils.hashId(getName());
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  /**
   * Set the name and update the id.
   * 
   * @param name
   *          The unique name of the source.
   */
  public void setName(final String name) {
    this.name = name;
    updateId();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public Source save() {
    final Source source = super.save();
    DAO.fireOnUpdated(this);
    return source;
  }

  @Override
  public void delete() {
    super.delete();
    DAO.fireOnRemoved(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    if (!(obj instanceof Source)) {
      return false;
    }
    final Source other = (Source) obj;
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("Source [id=%s, name=%s, description=%s]", id, name, description);
  }

}
