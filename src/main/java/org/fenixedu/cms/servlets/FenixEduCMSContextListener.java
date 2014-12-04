package org.fenixedu.cms.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Summary;

import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.executionCourse.ExecutionCourseListener;
import org.fenixedu.cms.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.cms.domain.executionCourse.SummaryListener;
import pt.ist.fenixframework.FenixFramework;

@WebListener
public class FenixEduCMSContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Signal.register(Summary.CREATE_SIGNAL, (DomainObjectEvent<Summary> event) -> {
            SummaryListener.updatePost(new Post(), event.getInstance());
        });
        FenixFramework.getDomainModel().registerDeletionListener(Summary.class, (summary)->{
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
        FenixFramework.getDomainModel().registerDeletionListener(ExecutionCourse.class, (executionCourse)->{
            if(executionCourse.getCmsSite()!=null) {
                executionCourse.getCmsSite().delete();
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
