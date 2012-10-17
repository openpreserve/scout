package eu.scape_project.watch.utils;

import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.domain.DictionaryItem;

import thewebsemantic.lazy.LazyList;

/**
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class LazyListSerializer extends SerializerBase<LazyList> {

  final Logger log = LoggerFactory.getLogger(getClass());

  public LazyListSerializer() {
    super(LazyList.class);
  }

  @Override
  public void serialize(LazyList list, JsonGenerator generator, SerializerProvider provider) throws IOException,
    JsonProcessingException {
    log.info("lazy list: " + list);
    generator.writeStartArray();
    final Iterator iterator = list.iterator();
    while (iterator.hasNext()) {
      final Object object = iterator.next();
      log.info("lazy list item: " + object);
      if (object instanceof DictionaryItem) {
        DictionaryItem item = (DictionaryItem) object;
        generator.writeStartObject();
        generator.writeStringField("key", item.getKey());
        generator.writeStringField("value", item.getValue());
        generator.writeEndObject();
      } else {
        provider.defaultSerializeValue(object, generator);
      }

    }
    generator.writeEndArray();
  }

}
