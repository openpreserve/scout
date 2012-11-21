package eu.scape_project.watch.domain;

public class Objective {

  private String url;

  private String measure;

  private String measureDescription;

  private String modality;

  private String qualifier;

  private String value;

  public Objective() {

  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMeasure() {
    return measure;
  }

  public void setMeasure(String measure) {
    this.measure = measure;
  }

  public String getMeasureDescription() {
    return measureDescription;
  }

  public void setMeasureDescription(String measureDescription) {
    this.measureDescription = measureDescription;
  }

  public String getModality() {
    return modality;
  }

  public void setModality(String modality) {
    this.modality = modality;
  }

  public String getQualifier() {
    return qualifier;
  }

  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String toString() {
    return "Objective [" + this.getUrl() + "] defines that: \nmeasure '" + this.getMeasure() + "' with description '"
        + this.getMeasureDescription() + "' " + this.getModality() + " have a \nvalue " + this.getQualifier()
        + this.getValue() + "'\n";
  }
}
