package eu.scape_project.watch.main;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.notification.NotificationService;

public class AssessmentService {

  private final NotificationService notificationService;

  public AssessmentService(final NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  public void assess(final Trigger trigger, final AsyncRequest request) {
    final Question question = trigger.getQuestion();

    if (question != null) {
      final RequestTarget target = question.getTarget();
      final String sparql = question.getSparql();

      final int count = DAO.REQUEST.count(target, sparql);
      if (count > 0) {
        for (Notification notification : trigger.getNotifications()) {
          notificationService.send(notification, question, null);
        }
      }
    }

    if (trigger.getPlan() != null) {
      // TODO external assessment of plan
    }
  }
}
