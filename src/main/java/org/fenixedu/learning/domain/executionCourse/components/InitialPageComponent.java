package org.fenixedu.learning.domain.executionCourse.components;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

@ComponentType(name = "InitialPage", description = "Provides the information needed for the initial page of an Execution Course")
public class InitialPageComponent implements CMSComponent {
    public final static int ANNOUNCEMENTS_TO_SHOW = 5;

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put(
                "professorships",
                executionCourse
                        .getProfessorshipsSet()
                        .stream()
                        .sorted(Comparator.comparing(Professorship::isResponsibleFor).reversed()
                                .thenComparing(Professorship.COMPARATOR_BY_PERSON_NAME)).collect(Collectors.toList()));
        globalContext.put("isStudent", isStudent(Authenticate.getUser()));
        globalContext.put("executionCourse", executionCourse);
    }

    private boolean isStudent(User user) {
        return user != null && user.getPerson() != null && user.getPerson().getStudent() != null;
    }
}
