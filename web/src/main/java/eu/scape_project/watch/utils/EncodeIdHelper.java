package eu.scape_project.watch.utils;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class EncodeIdHelper implements Helper<String> {

  @Override
  public CharSequence apply(String context, Options arg1) throws IOException {
    return KBUtils.encodeId(context);
  }

}
