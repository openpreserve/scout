package eu.scape_project.watch.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.scape_project.watch.domain.DictionaryItem;

/**
 * Help methods for the data model.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public final class ModelUtils {

  /**
   * Utility classes should not have public constructor.
   */
  private ModelUtils() {

  }

  /**
   * Convert a map to a list of entries.
   * 
   * @param map
   *          The Map.
   * @return A list of entries.
   */
  public static List<DictionaryItem> mapToEntryList(final Map<String, String> map) {
    final List<DictionaryItem> list = new ArrayList<DictionaryItem>();

    if (map != null) {
      for (Map.Entry<String, String> mapEntry : map.entrySet()) {
        list.add(new DictionaryItem(mapEntry.getKey(), mapEntry.getValue()));
      }
    }

    return list;
  }

  /**
   * Convert a list of entries to a map.
   * 
   * @param entryList
   *          the list of entries.
   * @return the map.
   */
  public static Map<String, String> entryListToMap(final Collection<DictionaryItem> entryList) {
    final Map<String, String> map = new HashMap<String, String>();

    if (entryList != null) {
      for (DictionaryItem entry : entryList) {
        map.put(entry.getKey(), entry.getValue());
      }
    }

    return map;
  }

}
