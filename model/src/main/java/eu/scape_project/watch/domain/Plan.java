package eu.scape_project.watch.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.utils.KBUtils;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * Reference to a preservation plan, to be used by external assessment.
 * 
 * External assessment will use not only the Trigger Condition but also
 * additional (global) knowledge that cannot be encoded into the Condition in
 * order to assess the information. This module will not be part of the Watch
 * component.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.PLAN)
@XmlAccessorType(XmlAccessType.FIELD)
public class Plan extends RdfBean<Plan> {

  /**
   * The unique identifier of the plan.
   */
  @XmlElement(required = true)
  private String planId;

  /**
   * Create a new empty plan.
   */
  public Plan() {
    super();
  }

  /**
   * Create a plan.
   * 
   * @param planId
   *          The unique plan identifier
   */
  public Plan(final String planId) {
    super();
    this.planId = planId;
  }
  
  @Id
  public String getId() {
    return KBUtils.encodeId(getPlanId());
  }

  public String getPlanId() {
    return planId;
  }

  public void setPlanId(final String planId) {
    this.planId = planId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.planId == null) ? 0 : this.planId.hashCode());
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
    final Plan other = (Plan) obj;
    if (this.planId == null) {
      if (other.planId != null) {
        return false;
      }
    } else if (!this.planId.equals(other.planId)) {
      return false;
    }
    return true;
  }

  @Override
  public Plan save() {
    final Plan plan = super.save();
    DAO.fireOnUpdated(this);
    return plan;
  }

  @Override
  public void delete() {
    super.delete();
    DAO.fireOnRemoved(this);
  }

  @Override
  public String toString() {
    return "Plan [id=" + planId + "]";
  }

}
