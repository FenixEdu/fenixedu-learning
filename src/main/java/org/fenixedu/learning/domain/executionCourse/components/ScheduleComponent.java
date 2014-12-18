package org.fenixedu.learning.domain.executionCourse.components;

import static org.fenixedu.bennu.core.security.Authenticate.getUser;
import static org.fenixedu.bennu.core.security.Authenticate.isLogged;

import java.util.Collection;

import org.fenixedu.academic.domain.Coordinator;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.ScheduleEventBean;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

@ComponentType(name = "Execution Course Schedule", description = "Schedule of an execution course")
public class ScheduleComponent implements CMSComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        if (hasPermissionToViewSchedule(executionCourse)) {
            globalContext.put("defaultView", "agendaWeek");
            globalContext.put("events", allEvents(executionCourse));
        }
    }

    private Collection<ScheduleEventBean> allEvents(ExecutionCourse executionCourse) {
        return ScheduleEventBean.forExecutionCourse(executionCourse);
    }

    private boolean hasPermissionToViewSchedule(ExecutionCourse executionCourse) {
        boolean isOpenPeriod = !executionCourse.getExecutionPeriod().isNotOpen();
        boolean isLogged = isLogged();
        boolean isAllocationManager = RoleType.RESOURCE_ALLOCATION_MANAGER.isMember(getUser());
        boolean isCoordinator =
                executionCourse.getDegreesSortedByDegreeName().stream()
                        .flatMap(degree -> degree.getCurrentCoordinators().stream()).map(Coordinator::getPerson)
                        .filter(coordinator -> coordinator.equals(getUser().getPerson())).findFirst().isPresent();
        return isOpenPeriod || (isLogged && (isAllocationManager || isCoordinator));
    }
}
