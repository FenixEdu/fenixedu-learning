package org.fenixedu.cms.domain.executionCourse.components;

import net.sourceforge.fenixedu.domain.Coordinator;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.ScheduleEventBean;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static net.sourceforge.fenixedu.domain.person.RoleType.RESOURCE_ALLOCATION_MANAGER;
import static org.fenixedu.bennu.core.security.Authenticate.getUser;
import static org.fenixedu.bennu.core.security.Authenticate.isLogged;
import static org.joda.time.DateTime.*;

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
        boolean isAllocationManager = getUser().getPerson().hasRole(RESOURCE_ALLOCATION_MANAGER);
        boolean isCoordinator = executionCourse.getDegreesSortedByDegreeName().stream()
                .flatMap(degree->degree.getCurrentCoordinators().stream()).map(Coordinator::getPerson)
                .filter(coordinator->coordinator.equals(getUser().getPerson())).findFirst().isPresent();
        return isOpenPeriod || (isLogged && (isAllocationManager || isCoordinator));
    }
}
