package eu.scape_project.watch.utils;

import eu.scape_project.watch.domain.DataType;

/**
 * Represents a config parameter of a plugin.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ConfigParameter {

  /**
   * The key of this config parameter.
   */
  private String key;

  /**
   * The default value of this config parameter. Can provide a default value.
   */
  private String value;

  /**
   * A human readable description of this config parameter.
   */
  private String description;

  /**
   * Whether or not the value for this config parameter is required for the
   * correct execution of the plugin.
   */
  private boolean required;

  /**
   * Whether or not the parameter value should be hidden or not (e.g.
   * passwords).
   */
  private boolean hidden;

  /**
   * The data type of this parameter. DataType.STRING by default
   */
  private DataType dataType;
  

  /**
   * No-arg constructor for serialization.
   */
  public ConfigParameter() {
    super();
  }

  /**
   * Inits a config parameter.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public ConfigParameter(final String key, final String value) {
    this.key = key;
    this.value = value;
    this.description = "";
    this.required = false;
    this.hidden = false;
    this.dataType = DataType.STRING;
  }

  /**
   * Inits a config parameter.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   * @param desc
   *          the description
   */
  public ConfigParameter(final String key, final String value, final String desc) {
    this(key, value);
    this.description = desc;
  }

  /**
   * Inits a config parameter.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   * @param req
   *          the required flag.
   */
  public ConfigParameter(final String key, final String value, final boolean req) {
    this(key, value);
    this.required = req;
  }

  /**
   * Inits a config parameter.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   * @param desc
   *          the description
   * @param req
   *          the reqired flag
   */
  public ConfigParameter(final String key, final String value, final String desc, final boolean req) {
    this(key, value, desc);
    this.required = req;
  }

  /**
   * Inits a config parameter.
   * 
   * @param key
   *          the key of the parameter
   * @param value
   *          the value of the parameter (could be the default value).
   * @param desc
   *          the description for this parameter.
   * @param req
   *          whether or not this parameter is required.
   * @param hidden
   *          whether or not the value of this parameter should be hidden.
   */
  public ConfigParameter(final String key, final String value, final String desc, final boolean req,
      final boolean hidden) {
    this(key, value, desc, req);
    this.hidden = hidden;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public boolean isRequired() {
    return this.required;
  }

  public void setRequired(final boolean required) {
    this.required = required;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

}
