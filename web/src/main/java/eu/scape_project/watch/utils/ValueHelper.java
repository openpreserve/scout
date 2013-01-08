package eu.scape_project.watch.utils;

import humanize.Humanize;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;
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
    final Boolean shortForm = options.hash("short", Boolean.FALSE);

    log.info("value: " + value);
    final StringBuilder builder = new StringBuilder();
    if (value instanceof URI) {
      final URI uriValue = (URI) value;
      builder.append("<a href='");
      builder.append(uriValue);
      builder.append("'>");
      builder.append(uriValue);
      builder.append("</a>");
    } else if (value instanceof Long) {
      final Long longValue = (Long) value;
      if (RenderingHint.STORAGE_VOLUME.equals(renderingHint)) {
        builder.append(Humanize.binaryPrefix(longValue));
      } else {
        builder.append(longValue);
      }
    } else if (value instanceof Double) {
      final Double doubleValue = (Double) value;
      if (RenderingHint.STORAGE_VOLUME.equals(renderingHint)) {
        builder.append(Humanize.binaryPrefix(doubleValue));
      } else {
        builder.append(doubleValue);
      }
    } else if (value instanceof Date) {
      if (RenderingHint.DATE_DAY.equals(renderingHint)) {
        final Date dateValue = (Date) ((Date) value).clone();
        final DateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyy");
        builder.append(format.format(dateValue));
      } else {
        final Date dateValue = (Date) ((Date) value).clone();
        final DateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyy, h:mm:ss a");
        builder.append(format.format(dateValue));
      }

    } else if (value instanceof LazyList) {
      final LazyList listValue = (LazyList) value;
      log.info("lazy list: " + Arrays.toString(listValue.toArray()));

      if (datatype.equals(DataType.STRING_DICTIONARY)) {
        if (shortForm) {
          builder.append("<span>");
          builder.append(listValue.size());
          builder.append(" key-value pairs</span>");
        } else {
          builder.append("<table class='table table-bordered table-condensed' style='margin:0'>");
          builder.append("<thead><th>Key</th><th>Value</th></thead>");
          for (Object item : listValue) {
            if (item instanceof DictionaryItem) {
              final DictionaryItem dictionaryItem = (DictionaryItem) item;
              builder.append("<tr>");
              builder.append("<td>");
              builder.append(dictionaryItem.getKey());
              builder.append("</td>");
              builder.append("<td>");
              builder.append(dictionaryItem.getValue());
              builder.append("</td>");
              builder.append("</tr>");
            } else {
              builder.append("<tr>");
              builder.append("<td>");
              builder.append(item);
              builder.append("</td>");
              builder.append("</tr>");
            }
          }
          builder.append("</table>");
        }
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
