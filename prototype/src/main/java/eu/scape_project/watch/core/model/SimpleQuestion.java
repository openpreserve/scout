package eu.scape_project.watch.core.model;

import java.util.List;

public class SimpleQuestion extends Question {

  private long asOfDate;

  private List<Filter> fileredBy;

  private QuestionType isAbout;

  public SimpleQuestion() {
    super();
  }

  public SimpleQuestion(QuestionType type, List<Filter> f, long asOf) {
    this();
    this.isAbout = type;
    this.fileredBy = f;
    this.asOfDate = asOf;

  }

  public long getAsOfDate() {
    return asOfDate;
  }

  public void setAsOfDate(long asOfDate) {
    this.asOfDate = asOfDate;
  }

  public List<Filter> getFileredBy() {
    return fileredBy;
  }

  public void setFileredBy(List<Filter> fileredBy) {
    this.fileredBy = fileredBy;
  }

  public QuestionType getIsAbout() {
    return isAbout;
  }

  public void setIsAbout(QuestionType isAbout) {
    this.isAbout = isAbout;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (asOfDate ^ (asOfDate >>> 32));
    result = prime * result + ((fileredBy == null) ? 0 : fileredBy.hashCode());
    result = prime * result + ((isAbout == null) ? 0 : isAbout.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SimpleQuestion other = (SimpleQuestion) obj;
    if (asOfDate != other.asOfDate)
      return false;
    if (fileredBy == null) {
      if (other.fileredBy != null)
        return false;
    } else if (!fileredBy.equals(other.fileredBy))
      return false;
    if (isAbout != other.isAbout)
      return false;
    return true;
  }
}
