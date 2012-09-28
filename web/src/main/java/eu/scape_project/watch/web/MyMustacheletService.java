package eu.scape_project.watch.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

/**
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 *         Based on https://github.com/spullara/mustachelet
 * 
 */
public class MyMustacheletService extends HttpServlet implements Filter {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * DEFINE AVAILABLE MUSTACHELETS.
   */
  private static final List<Class<? extends Mustachelet>> MUSTACHELETS = Arrays.asList(Index.class, Browse.class,
    BrowseType.class, BrowseEntity.class, Administration.class, CreateSourceAdaptor.class, CreateSource.class,
    BrowseAdaptor.class);

  private static final boolean DISABLE_CACHE = Boolean.parseBoolean(System.getProperty(
    "mustache.servlet.cache.disable", "false"));

  private static final String DEFAULT_RESOURCE_ROOT = "/eu/scape_project/watch/web/";

  /**
   * To log messages.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Cash of path patterns to mustachelets.
   */
  private Map<Pattern, Map<HttpMethod.Type, Class<?>>> pathMap = new HashMap<Pattern, Map<HttpMethod.Type, Class<?>>>();

  /**
   * Cash of mustachelets to mustaches.
   */
  private Map<Class<?>, Mustache> mustacheMap = new HashMap<Class<?>, Mustache>();

  /**
   * Cash of mustachelets to controllers.
   */
  private Map<Class<?>, Map<HttpMethod.Type, Method>> controllerMap = new HashMap<Class<?>, Map<HttpMethod.Type, Method>>();

  /**
   * Filter config.
   */
  private FilterConfig filterConfig;

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
    IOException {

    if (DISABLE_CACHE) {
      logger.warn("Cache is disabled via JVM system attributes");
      init();
    }

    final boolean successful = execute(resp, req);
    if (!successful) {
      resp.sendError(404, "Not found");
    }
  }

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
    init();
  }

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);
  }

  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
    throws IOException, ServletException {
    final HttpServletResponse resp = (HttpServletResponse) response;
    final HttpServletRequest req = (HttpServletRequest) request;
    if (execute(resp, req)) {
      return;
    }
    chain.doFilter(request, response);
  }

  private boolean execute(final HttpServletResponse resp, final HttpServletRequest req) throws IOException {
    resp.setHeader("Server", "Mustachelet/0.1");
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");

    final String requestURI = req.getRequestURI();
    final String contextPath = req.getContextPath();
    String pathInfo = req.getPathInfo();
    final String basePath = requestURI.substring(0, requestURI.length() - pathInfo.length());

    if (pathInfo == null || pathInfo.equals("")) {
      pathInfo = "/";
    }

    logger.info("Request URI: {}", requestURI);
    logger.info("Context path: {}", contextPath);
    logger.info("Path info: {}", pathInfo);
    logger.info("Base path: {}", basePath);

    for (Map.Entry<Pattern, Map<HttpMethod.Type, Class<?>>> entry : pathMap.entrySet()) {
      final Matcher matcher = entry.getKey().matcher(pathInfo);
      if (matcher.matches()) {
        final Map<HttpMethod.Type, Class<?>> methodClassMap = entry.getValue();
        String httpMethod = req.getMethod();
        final boolean head;
        if (httpMethod.equals("HEAD")) {
          head = true;
          httpMethod = "GET";
        } else {
          head = false;
        }
        final HttpMethod.Type type = HttpMethod.Type.valueOf(httpMethod);
        logger.info("HTTP type is {}", type);

        // Inject request and response into mustachelet
        final Injector injector = Guice.createInjector(new Module() {
          @Override
          public void configure(final Binder binder) {
            binder.bind(Matcher.class).toInstance(matcher);
            binder.bind(HttpServletRequest.class).toInstance(req);
            binder.bind(HttpServletResponse.class).toInstance(resp);
            binder.bind(String.class).toInstance(basePath);
          }
        });

        final Class<?> mustachelet = methodClassMap.get(type);
        logger.info("Mustachelet is {}", mustachelet);

        final Object o = injector.getInstance(mustachelet);
        if (o != null) {
          final Map<HttpMethod.Type, Method> typeMethodMap = controllerMap.get(mustachelet);
          if (typeMethodMap != null) {
            final Method method = typeMethodMap.get(type);
            if (method != null) {
              Object invoke;
              try {
                invoke = method.invoke(o);
                if (invoke instanceof Boolean && !((Boolean) invoke)) {
                  return true;
                }
              } catch (final Exception e) {
                logger.error("Error invoking mustache controller method: " + method, e);
                resp.setStatus(500);
                return true;
              }
            }
          }
          if (head) {
            resp.setStatus(200);
            return true;
          }
          final Mustache mustache = mustacheMap.get(mustachelet);
          try {
            Writer writer = resp.getWriter();
            writer = mustache.execute(writer, o);
            resp.setStatus(200);
            writer.close();
            return true;
          } catch (final MustacheException e) {
            resp.setStatus(500);
            logger.error("Error executing mustache", e);
            return true;
          }
        } else {
          resp.setStatus(405);
        }
      }
    }
    return false;
  }

  /**
   * Load mustachelets and compile mustaches.
   */
  public void init() throws ServletException {
    String moduleClass;
    if (filterConfig != null) {
      moduleClass = filterConfig.getInitParameter("module");
    } else {
      moduleClass = getInitParameter("module");
    }
    if (moduleClass != null) {
      try {
        Guice.createInjector((Module) Class.forName(moduleClass).newInstance()).injectMembers(this);
      } catch (final Exception e) {
        throw new ServletException("Failed to initialize", e);
      }
    }

    final DefaultMustacheFactory mc = new DefaultMustacheFactory(DEFAULT_RESOURCE_ROOT);

    for (Class<?> mustachelet : MUSTACHELETS) {
      final Path annotation = mustachelet.getAnnotation(Path.class);
      if (annotation == null) {
        throw new ServletException("No Path annotation present on: " + mustachelet.getCanonicalName());
      }
      final Template template = mustachelet.getAnnotation(Template.class);
      if (template == null) {
        throw new ServletException("You must specify a template on: " + mustachelet.getCanonicalName());
      }
      final HttpMethod httpMethod = mustachelet.getAnnotation(HttpMethod.class);
      final String regex = annotation.value();
      final Map<HttpMethod.Type, Class<?>> methodClassMap = new HashMap<HttpMethod.Type, Class<?>>();
      if (httpMethod == null) {
        methodClassMap.put(HttpMethod.Type.GET, mustachelet);
      } else {
        for (HttpMethod.Type type : httpMethod.value()) {
          methodClassMap.put(type, mustachelet);
        }
      }
      final Map<HttpMethod.Type, Class<?>> put = pathMap.put(Pattern.compile(regex), methodClassMap);
      if (put != null) {
        throw new ServletException("Duplicate path: " + mustachelet + " and " + put);
      }
      try {
        // File file = new File(root, template.value());
        final InputStream stream = mustachelet.getResourceAsStream(template.value());
        if (stream == null) {
          throw new ServletException("Template file does not exist: [" + mustachelet.getClass().getName() + "]"
            + template.value());
        }

        final Mustache mustache = mc.compile(new InputStreamReader(stream), template.value());
        mustacheMap.put(mustachelet, mustache);
        logger.info("Added mustachelet {}", mustachelet);
      } catch (final Exception e) {
        throw new ServletException("Failed to compile template: " + template.value(), e);
      }
      for (Method method : mustachelet.getDeclaredMethods()) {
        final Controller controller = method.getAnnotation(Controller.class);
        if (controller != null) {
          method.setAccessible(true);
          Map<HttpMethod.Type, Method> typeMethodMap = controllerMap.get(mustachelet);
          if (typeMethodMap == null) {
            typeMethodMap = new HashMap<HttpMethod.Type, Method>();
            controllerMap.put(mustachelet, typeMethodMap);
          }
          for (HttpMethod.Type type : controller.value()) {
            typeMethodMap.put(type, method);
          }
        }
      }
    }
  }

  public void destroy() {
  }
}
