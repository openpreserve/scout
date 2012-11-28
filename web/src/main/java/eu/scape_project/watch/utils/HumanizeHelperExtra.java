package eu.scape_project.watch.utils;

import humanize.ICUHumanize;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.Validate;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public enum HumanizeHelperExtra implements Helper<Object> {
  duration {
    @Override
    public CharSequence apply(final Object value, final Options options) throws IOException {
      Validate.isTrue(value instanceof Number, "found '%s', expected: 'number'");
      return ICUHumanize.duration((Number) value);
    }
  },

  durationMS {
    @Override
    public CharSequence apply(final Object value, final Options options) throws IOException {
      Validate.isTrue(value instanceof Number, "found '%s', expected: 'number'");
      final Number millis = (Number) value;
      final Number seconds = millis.longValue() / 1000;
      return ICUHumanize.duration(seconds);
    }
  };

  /**
   * Resolve a locale.
   * 
   * @param options
   *          The helper's options.
   * @return A locale.
   */
  protected static Locale resolveLocale(final Options options) {
    String locale = options.hash("locale", Locale.getDefault().toString());
    return LocaleUtils.toLocale(locale);
  }
  
  /**
   * Register all the humanize helpers.
   *
   * @param handlebars The helper's owner.
   */
  public static void register(final Handlebars handlebars) {
    Validate.notNull(handlebars, "A handlebars object is required.");
    for (HumanizeHelperExtra helper : values()) {
      handlebars.registerHelper(helper.name(), helper);
    }
  }
}
