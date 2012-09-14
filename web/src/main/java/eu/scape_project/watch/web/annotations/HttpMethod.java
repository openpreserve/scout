package eu.scape_project.watch.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is the regex for the path that the mustachelet serves.
 * <p/>
 * User: sam
 * Date: 12/21/10
 * Time: 2:24 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HttpMethod {
  enum Type {
    GET, POST, HEAD
  }
  Type[] value() default Type.GET;
}
