package eu.scape_project.watch.utils;

import humanize.Humanize;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.lazy.LazyList;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.RenderingHint;

/**
 * Template helper to better render property values.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class ValueHelper implements Helper<PropertyValue> {

  /**
   * Logger.
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public CharSequence apply(final PropertyValue pv, final Options options) throws IOException {

    final Object value = pv.getValue();
    final DataType datatype = pv.getProperty().getDatatype();
    final RenderingHint renderingHint = pv.getProperty().getRenderingHint();

    log.info("value: " + value);
    final StringBuilder builder = new StringBuilder();
    if (value instanceof URI) {
      final URI uriValue = (URI) value;
      builder.append("<a href='");
      builder.append(uriValue);
      builder.append("'>");
      builder.append(uriValue);
      builder.append("</a>");
    } else if (value instanceof Integer) {
      final Integer integerValue = (Integer) value;
      if (RenderingHint.STORAGE_VOLUME.equals(renderingHint)) {
        builder.append(Humanize.binaryPrefix(integerValue));
      } else {
        builder.append(integerValue);
      }
    } else if (value instanceof Date) {
      final Date dateValue = (Date) ((Date) value).clone();
      final DateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyy, h:mm:ss a");
//      builder.append(Humanize.naturalDay(dateValue));
      builder.append(format.format(dateValue));
    } else if (value instanceof LazyList) {
      final LazyList listValue = (LazyList) value;

      if (datatype.equals(DataType.STRING_DICTIONARY)) {
        builder.append("<dl class=\"dl-horizontal\">");
        for (Object item : listValue) {
          if (item instanceof DictionaryItem) {
            final DictionaryItem dictionaryItem = (DictionaryItem) item;
            builder.append("<dt>");
            builder.append(dictionaryItem.getKey());
            builder.append("</dt>");
            builder.append("<dd>");
            builder.append(dictionaryItem.getValue());
            builder.append("</dd>");
          } else {
            builder.append("<dd>");
            builder.append(item);
            builder.append("</dd>");
          }
        }
        builder.append("</dl>");
      } else {
        builder.append("<ul>");
        for (Object item : listValue) {
          builder.append(item);
        }
        builder.append("</ul>");
      }
    } else {
      builder.append(value);
    }

    return builder.toString();
  }

}
