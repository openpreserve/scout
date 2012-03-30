package eu.scape_project.watch.rest.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * Provider that configures Jackson to better detect methods and properties to
 * serialize JSON.
 * 
 * 
 * @author Wordnik
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JSONContextResolver extends org.codehaus.jackson.jaxrs.JacksonJsonProvider {
  
  /**
   * Instance holder.
   */
  @SuppressWarnings("deprecation")
  private static final class ObjectMapperHolder {
    /**
     * Cannot be instantiated.
     */
    private ObjectMapperHolder() {
    }

    /**
     * The instance.
     */
    public static final ObjectMapper COMMON_MAPPER = new ObjectMapper();
    
    static {
      AnnotationIntrospector jackson = new JacksonAnnotationIntrospector();
      AnnotationIntrospector jaxb = new JaxbAnnotationIntrospector();
      AnnotationIntrospector pair = new AnnotationIntrospector.Pair(jaxb, jackson);
      COMMON_MAPPER.getSerializationConfig().setAnnotationIntrospector(jaxb);
      COMMON_MAPPER.getDeserializationConfig().setAnnotationIntrospector(pair);
      COMMON_MAPPER.getDeserializationConfig().set(Feature.AUTO_DETECT_SETTERS, true);
      COMMON_MAPPER.configure(Feature.AUTO_DETECT_SETTERS, true);
      COMMON_MAPPER.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
      COMMON_MAPPER.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      COMMON_MAPPER.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
  }

  /**
   * Get singleton instance.
   * 
   * @return The singleton instance
   */
  public static ObjectMapper getCommonMapper() {
    return ObjectMapperHolder.COMMON_MAPPER;
  }

  public JSONContextResolver() {
    super.setMapper(getCommonMapper());
  }
}
