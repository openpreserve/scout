package eu.scape_project.watch.components.adaptors.c3po;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.tdb.store.Hash;

/**
 * A simple helper class used to store some statics on property bases while
 * adapting the profile values to the internal knowledge base.
 * 
 * @author peter
 * 
 */
public class Statistics {

  /**
   * The name of the property.
   */
  private String property;

  /**
   * The mode of the property.
   */
  private String mode;

  /**
   * The average of the property if possible to calculate, or empty.
   */
  private String avg;

  /**
   * The mean of the property if possible to calculate, or empty.
   */
  private String mean;

  /**
   * The minimum of the property if possible to calculate, or empty.
   */
  private String min;

  /**
   * The maximum of the property if possible to calculate, or empty.
   */
  private String max;

  /**
   * The standard deviation of the property if possible to calculate, or empty.
   */
  private String sd;

  /**
   * Stores a distribution of a property. E.g. [pdf - 100, doc - 32, txt - 42]
   * or in case of numerics ["0-100" - 123, "101-200" - 3223]
   */
  private Map<String, Integer> distribution;

  /**
   * Initializes the statistics for the given property.
   * 
   * @param property
   *          the property name
   * @param mode
   *          the statistical mode of the property.
   */
  public Statistics(final String property, final String mode) {
    this.property = property;
    this.mode = mode;
    this.distribution = new HashMap<String, Integer>();
  }

  /**
   * Adds a distribution item to the distribution map.
   * 
   * @param key
   *          the key of the distribution (e.g, pdf, doc, or a statistical
   *          class, eg. 0 - 100Bytes)
   * @param value
   *          the value of the distribution item.
   */
  public void addDistributionItem(final String key, final Integer value) {
    this.distribution.put(key, value);
  }

  public String getProperty() {
    return this.property;
  }

  public void setProperty(final String property) {
    this.property = property;
  }

  public String getMode() {
    return this.mode;
  }

  public void setMode(final String mode) {
    this.mode = mode;
  }

  public String getAvg() {
    return avg;
  }

  public void setAvg(final String avg) {
    this.avg = avg;
  }

  public String getMean() {
    return this.mean;
  }

  public void setMean(final String mean) {
    this.mean = mean;
  }

  public String getMin() {
    return this.min;
  }

  public void setMin(final String min) {
    this.min = min;
  }

  public String getMax() {
    return this.max;
  }

  public void setMax(final String max) {
    this.max = max;
  }

  public String getSd() {
    return this.sd;
  }

  public void setSd(final String sd) {
    this.sd = sd;
  }

  public Map<String, Integer> getDistribution() {
    return this.distribution;
  }

  public void setDistribution(final Map<String, Integer> distribution) {
    this.distribution = distribution;
  }

}
