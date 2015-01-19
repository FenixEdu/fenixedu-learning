package org.fenixedu.learning.domain.executionCourse.components;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

@ComponentType(name = "EvaluationMethods", description = "Evaluation Methods for an Execution Course")
public class EvaluationMethodsComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("evaluationMethod", executionCourse.getEvaluationMethod());
        globalContext.put("evaluationMethodText", executionCourse.getEvaluationMethodText());
    }

}
