package eu.scape_project.watch.core.model;


public class Property {

  private String name;

  private String description;

  private DataType datatype;

  public Property() {
    super();
  }

  public Property(String n, String d) {
    this.name = n;
    this.description = d;
  }

  public Property(String n, String d, DataType dt) {
    this(n, d);
    this.datatype = dt;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Property other = (Property) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  public DataType getDatatype() {
    return datatype;
  }

  public void setDatatype(DataType datatype) {
    this.datatype = datatype;
  }

}
