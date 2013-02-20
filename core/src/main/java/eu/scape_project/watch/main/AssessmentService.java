package eu.scape_project.watch.main;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.QueryBinding;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.notification.NotificationService;

public class AssessmentService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final NotificationService notificationService;

  public AssessmentService(final NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  public void assess(final Trigger trigger, final AsyncRequest request) {
    log.info("Assessing trigger {}", trigger);
    final Question question = trigger.getQuestion();

    if (question != null) {
      final RequestTarget target = question.getTarget();
      final String sparql = question.getSparql();
      final List<QueryBinding> bindings = question.getBindings();
      final QuerySolutionMap querySolutionMap = DAO.QUESTION_TEMPLATE_PARAMETER.parseBindings(bindings);

      final int count = DAO.REQUEST.count(target, sparql, querySolutionMap);
      if (count > 0) {
        log.info("Sending notifications for trigger {}", trigger);
        for (Notification notification : trigger.getNotifications()) {
          notificationService.send(notification, trigger, request);
        }
      }
    }

    if (trigger.getPlan() != null) {
      // TODO external assessment of plan
    }
  }
}
