package org.fenixedu.learning.domain.executionCourse.components;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

@ComponentType(name = "Execution Course Schedule", description = "Schedule of an execution course")
public class ScheduleComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("defaultView", "agendaWeek");
        globalContext.put("eventsUrl", CoreConfiguration.getConfiguration().applicationUrl()
                + "/api/fenixedu-learning/events/executionCourse/" + executionCourse.getExternalId());
        globalContext.put("dayToShow", ISODateTimeFormat.date().print(LocalDate.now()));
    }

}
