package eu.scape_project.watch.adaptor.pronom.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

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
        PropertyValue value = this.getPropertyValue(binding, name, formattype);
        if (value != null) {
          value.setEntity(format);
          result.add(value);
        }

      }
    }

    return result;
  }

  private Entity getEntity(EntityType et, String name) {
    return new Entity(et, name);

  }

  private PropertyValue getPropertyValue(JSONObject binding, String name, EntityType et) {
    JSONObject var = binding.getJSONObject(name);

    if (!var.isEmpty()) {

      System.out.println(name + " " + var);
      String type = var.getString("type");
      String value = var.getString("value");
      String datatype = var.optString("datatype", type);
      // TODO based on datatye set different vlalue;
      try {
        PropertyValue v = new PropertyValue();
        DataType dt = this.getDataType(datatype);
        Property p = new Property(et, name, name, dt);
        v.setProperty(p);

        this.setValue(value, v, dt);

        return v;
      } catch (UnsupportedDataTypeException e) {
        e.printStackTrace();
      } catch (InvalidJavaClassForDataTypeException e) {
        e.printStackTrace();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  private void setValue(String v, PropertyValue pv, DataType dt) throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException, URISyntaxException, ParseException {
    switch (dt) {
      case STRING:
        pv.setValue(v, String.class);
        break;
      case DATE:
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(v);
        pv.setValue(date, Date.class);
        break;
      case URI:
        pv.setValue(new URI(v), URI.class);
        break;
      default:
    }
  }

  private DataType getDataType(String datatype) {
    if (datatype == null || datatype.equals("") || datatype.equals("literal")) {
      return DataType.STRING;
    }

    if (datatype.equals("http://www.w3.org/2001/XMLSchemadate")) {
      return DataType.DATE;
    }

    if (datatype.equals("uri")) {
      return DataType.URI;
    }

    return DataType.STRING;
  }
}
