package eu.scape_project.watch.domain;

public class QueryBinding {

  private static final char SEPARATOR = '|';

  /**
   * Parse an string encoded query binding.
   * 
   * @param stringEncoded
   *          The query binding in encoded form.
   * @return The query binding or <code>null</code> if no separator found.
   */
  public static QueryBinding valueOf(final String stringEncoded) {
    final int indexOfSeparator = stringEncoded.indexOf(SEPARATOR);

    QueryBinding ret = null;

    if (indexOfSeparator >= 0) {
      final String key = stringEncoded.substring(0, indexOfSeparator);
      final String value = stringEncoded.substring(indexOfSeparator + 1);
      ret = new QueryBinding(key, value);
    }
    return ret;
  }

  private String key;
  private String value;

  public QueryBinding(String key, String value) {
    super();
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("QueryBinding [key=%s, value=%s]", key, value);
  }
  

}
