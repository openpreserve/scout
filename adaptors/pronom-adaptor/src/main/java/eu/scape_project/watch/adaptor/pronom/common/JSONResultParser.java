package eu.scape_project.watch.adaptor.pronom.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.RenderingHint;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * Parses the Json Result of the Pronom Query Service into Scouts internal
 * datamodel.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class JSONResultParser {

  private static final Logger LOG = LoggerFactory.getLogger(JSONResultParser.class);

  private ResultProcessingDispatcher processDispatcher;
  
  private boolean valuesExisted;
  
  public JSONResultParser(final ResultProcessingDispatcher dispatcher) {
    this.processDispatcher = dispatcher;
    this.valuesExisted = false;
  }

  /**
   * Parses the result string from the prepared query and stores the bindings
   * into {@link PropertyValue} objects.
   * 
   * @param json
   *          the json response of the pronom service.
   * @return the list of property values containing the formats.
   */
  public List<PropertyValue> parse(final String json) {
    LOG.trace("Parsing json: {}", json);
    final List<PropertyValue> result = new ArrayList<PropertyValue>();
    final JSONObject obj = (JSONObject) JSONSerializer.toJSON(json);
    final JSONObject head = obj.getJSONObject("head");
    final JSONArray vars = head.getJSONArray("vars");
    final JSONArray bindings = obj.getJSONObject("results").getJSONArray("bindings");
    final EntityType formattype = new EntityType("format", "Represents a file format");

    if (bindings.size() == 0) {
      this.valuesExisted = false;
    } else {
      this.valuesExisted = true;
    }
    
    int newFormats = 0;
    
    for (int i = 0; i < bindings.size(); i++) {
      final JSONObject binding = bindings.getJSONObject(i);
      final boolean process = this.processDispatcher.process(binding.toString());
      

      if (process) {
        String entityName = binding.getJSONObject(vars.getString(0)).getString("value");
        String mime = null;
        String version = null;
        newFormats++;
        
        LOG.trace("parsing values for format: '{}'", entityName);
        final Entity format = new Entity(formattype, entityName);

        for (int j = 1; j < vars.size(); j++) {
          final String name = vars.getString(j);
          final PropertyValue value = this.getPropertyValue(binding, name, formattype);
          if (value != null) {
            LOG.trace("value for property '{}' parsed successfully: '{}'", value.getProperty().getName(),
                value.getValue());
            value.setEntity(format);
            result.add(value);

            if (value.getProperty().getName().equals("mime")) {
              mime = (String) value.getValue();
            }

            if (value.getProperty().getName().equals("version")) {
              version = (String) value.getValue();
            }
          }
        }

        this.updateEntityName(format, mime, version);
      }
    }
    
    LOG.debug("Processed {} new json bindings", newFormats);

    return result;
  }
  
  public boolean shouldContinueCrawl() {
    return this.valuesExisted;
  }

  private void updateEntityName(Entity format, String mime, String version) {
    String name = format.getName();

    if (mime == null && version == null) {
      return;
    }

    if (mime != null && version == null) {
      name += "[" + mime + "]";
    }

    if (mime == null && version != null) {
      name += "[version=" + version + "]";
    }

    if (mime != null && version != null) {
      String vers = "; version=" + version;
      if (mime.contains(vers)) {
        name += "[" + mime + "]";
      } else {
        name += "[" + mime + vers + "]";
      }
    }

    format.setName(name);

  }

  /**
   * Gets the {@link PropertyValue} out of the binding. The binding consists of
   * an array of properties in the following form:
   * 
   * "property": { "type": "typed-literal", "value": "some value", "datatype":
   * "some datatype" }
   * 
   * @param binding
   *          the binding.
   * @param name
   *          the name of the property.
   * @param et
   *          the entity type of the property.
   * @return the property value object or null if no such property was present
   *         in the binding.
   */
  private PropertyValue getPropertyValue(JSONObject binding, String name, EntityType et) {
    JSONObject var = binding.getJSONObject(name);

    if (!var.isEmpty()) {
      final String type = var.getString("type");
      final String value = var.getString("value");
      final DataType dt = this.getDataType(var, type);

      try {
        final PropertyValue v = new PropertyValue();
        final Property p = new Property(et, name, name, dt);
        v.setProperty(p);
        
        if (dt == DataType.DATE) {
          p.setRenderingHint(RenderingHint.DATE_DAY);
        }

        this.setValue(value, v, dt);

        return v;

      } catch (UnsupportedDataTypeException e) {
        LOG.error("An error occurred while extracting '{}': {}", name, e.getMessage());
      } catch (InvalidJavaClassForDataTypeException e) {
        LOG.error("An error occurred while extracting '{}': {}", name, e.getMessage());
      } catch (URISyntaxException e) {
        LOG.error("An error occurred while extracting '{}': {}", name, e.getMessage());
      } catch (ParseException e) {
        LOG.error("An error occurred while extracting '{}': {}", name, e.getMessage());
      }
    }

    return null;
  }

  /**
   * Corrects the type of the value of the {@link PropertyValue} and sets it.
   * 
   * @param v
   *          the value as a string.
   * @param pv
   *          the propert value.
   * @param dt
   *          the desired datatype.
   * 
   * @throws UnsupportedDataTypeException
   *           propagated from {@link PropertyValue#setValue(Object, Class)}
   * @throws InvalidJavaClassForDataTypeException
   *           propagated from {@link PropertyValue#setValue(Object, Class)}
   * @throws URISyntaxException
   *           if the type is a uri but the value is not a real uri.
   * @throws ParseException
   *           if the type is a date, but the value is not in the form of
   *           yyyy-MM-dd.
   */
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
        pv.setValue(v, String.class);
        break;
    }
  }

  /**
   * Gets the datatype of the property of a binding.
   * 
   * @param var
   *          the json property
   * @param type
   *          the type of the value in the json property.
   * @return the {@link DataType}
   */
  private DataType getDataType(JSONObject var, String type) {

    if (type.equals("literal")) {
      return DataType.STRING;
    } else if (type.equals("uri")) {
      return DataType.URI;
    }

    if (type.equals("typed-literal")) {
      String datatype = var.getString("datatype");

      if (datatype == null || datatype.equals("")) {
        return DataType.STRING;
      }

      if (datatype.equals("http://www.w3.org/2001/XMLSchemadate")) {
        return DataType.DATE;
      }

      if (datatype.equals("uri")) {
        return DataType.URI;
      }
    }

    return DataType.STRING;
  }
}
