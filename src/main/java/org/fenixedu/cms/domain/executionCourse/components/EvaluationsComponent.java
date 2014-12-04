package org.fenixedu.cms.domain.executionCourse.components;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.fenixedu.domain.Exam;
import net.sourceforge.fenixedu.domain.ExecutionCourse;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "Evaluations", description = "Evaluations for an Execution Course")
public class EvaluationsComponent implements CMSComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("evaluations", executionCourse.getAssociatedEvaluationsSet());
        globalContext.put("comment", executionCourse.getComment());
        globalContext.put("onlineTests", executionCourse.getAssociatedOnlineTests());
        globalContext.put("adHocEvaluations", executionCourse.getOrderedAssociatedAdHocEvaluations());
        globalContext.put("projects", executionCourse.getAssociatedProjects());
        globalContext.put("publishedExams", publishedExams(executionCourse));
        globalContext.put("writtenTests", executionCourse.getAssociatedWrittenTests());
    }

    private List<Exam> publishedExams(ExecutionCourse executionCourse) {
        return executionCourse.getAssociatedExams().stream().filter(e -> e.isExamsMapPublished()).collect(Collectors.toList());
    }

}
