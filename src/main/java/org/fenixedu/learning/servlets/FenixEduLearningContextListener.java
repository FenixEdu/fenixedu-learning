package org.fenixedu.learning.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Summary;

import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseListener;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.learning.domain.executionCourse.SummaryListener;
import pt.ist.fenixframework.FenixFramework;

@WebListener
public class FenixEduLearningContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Signal.register(Summary.CREATE_SIGNAL, (DomainObjectEvent<Summary> event) -> {
            Summary summary = event.getInstance();
            SummaryListener.updatePost(new Post(summary.getExecutionCourse().getCmsSite()), summary);
        });
        FenixFramework.getDomainModel().registerDeletionListener(Summary.class, (summary) -> {
            Post post = summary.getPost();
            summary.setPost(null);
            post.delete();
        });
        Signal.register(Summary.EDIT_SIGNAL, (DomainObjectEvent<Summary> event) -> {
            SummaryListener.updatePost(event.getInstance().getPost(), event.getInstance());
        });

        Signal.register(ExecutionCourse.CREATED_SIGNAL, (DomainObjectEvent<ExecutionCourse> event) -> {
            ExecutionCourseListener.create(event.getInstance());
        });
        FenixFramework.getDomainModel().registerDeletionListener(ExecutionCourse.class, (executionCourse) -> {
            if (executionCourse.getCmsSite() != null) {
                executionCourse.getCmsSite().delete();
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        
    }
}
