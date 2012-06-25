package eu.scape_project.watch.adaptor.pronom.common;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.PropertyValue;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class JSONResultParser {

  public List<PropertyValue> parse(final String json) {
    final List<PropertyValue> result = new ArrayList<PropertyValue>();
    final JSONObject obj = (JSONObject) JSONSerializer.toJSON(json);
    final JSONObject head = obj.getJSONObject("head");
    final JSONArray vars = head.getJSONArray("vars");
    final JSONArray bindings = obj.getJSONObject("results").getJSONArray("bindings");
    final EntityType formattype = new EntityType("format", "represents a format");

    for (int i = 0; i < bindings.size(); i++) {
      final JSONObject binding = bindings.getJSONObject(i);

      // relies on the fact that name is the first binding...
      // should be corrected...
      Entity format = this.getEntity(formattype, binding.getJSONObject(vars.getString(0)).getString("value"));
      System.out.println("Values for Format: " + format.getName());
      for (int j = 1; j < vars.size(); j++) {
        String name = vars.getString(j);
        PropertyValue value = this.getPropertyValue(binding, name);

        if (value != null) {
          value.setEntity(format);
          result.add(value);
        }

      }
    }

    return new ArrayList<PropertyValue>();
  }

  private Entity getEntity(EntityType et, String name) {
    return new Entity(et, name);

  }

  private PropertyValue getPropertyValue(JSONObject binding, String name) {
    PropertyValue v = null;
    JSONObject var = binding.getJSONObject(name);

    if (!var.isEmpty()) {
      System.out.println(var);
      String type = var.getString("type");
      String value = var.getString("value");
      String datatype = var.optString("datatype", "string");

      // setup value and return it...
    }

    return null;
  }

}
