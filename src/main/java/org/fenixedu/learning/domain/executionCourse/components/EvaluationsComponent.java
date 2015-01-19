package org.fenixedu.learning.domain.executionCourse.components;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Exam;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

@ComponentType(name = "Evaluations", description = "Evaluations for an Execution Course")
public class EvaluationsComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("comment", executionCourse.getComment());
        globalContext.put("adHocEvaluations", executionCourse.getOrderedAssociatedAdHocEvaluations());
        globalContext.put("projects", executionCourse.getAssociatedProjects());
        globalContext.put("publishedExams", publishedExams(executionCourse));
        globalContext.put("writtenTests", executionCourse.getAssociatedWrittenTests());
    }

    private List<Exam> publishedExams(ExecutionCourse executionCourse) {
        return executionCourse.getAssociatedExams().stream().filter(e -> e.isExamsMapPublished()).collect(Collectors.toList());
    }

}
