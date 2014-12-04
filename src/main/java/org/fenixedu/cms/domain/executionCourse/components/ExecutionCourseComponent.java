package org.fenixedu.cms.domain.executionCourse.components;

import net.sourceforge.fenixedu.domain.ExecutionCourse;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "ExecutionCourse", description = "Provides the Execution Course information")
public class ExecutionCourseComponent implements CMSComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("executionCourse", executionCourse);
    }

}
