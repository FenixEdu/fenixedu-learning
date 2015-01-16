package org.fenixedu.learning.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.fenixedu.academic.domain.Coordinator;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Project;
import org.fenixedu.academic.domain.WrittenEvaluation;
import org.fenixedu.academic.util.EvaluationType;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.learning.domain.ScheduleEventBean;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/fenixedu-learning/events")
public class EventsResource {

    @GET
    @Path("/executionCourse/{course}")
    @Produces("application/json; charset=utf-8")
    public String executionCourseEvents(@PathParam("course") ExecutionCourse course, @QueryParam("start") String start,
            @QueryParam("end") String end) {
        return toJson(
                hasPermissionToViewSchedule(course) ? ScheduleEventBean.forExecutionCourse(course, getInterval(start, end)) : Collections
                        .emptySet()).toString();
    }

    @GET
    @Path("/degree/evaluations/{degree}")
    @Produces("application/json; charset=utf-8")
    public String degreeEvaluationsEvents(@PathParam("degree") Degree degree, @QueryParam("start") String start,
            @QueryParam("end") String end) {
        return toJson(allPublicEvaluations(degree, getInterval(start, end))).toString();
    }

    private Interval getInterval(String start, String end) {
        DateTime beginDate;
        DateTime endDate;

        if (Strings.isNullOrEmpty(start)) {
            DateTime now = new DateTime();
            beginDate = now.withDayOfWeek(DateTimeConstants.MONDAY).withHourOfDay(0).withMinuteOfHour(0);
            endDate = now.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1).withHourOfDay(0).withMinuteOfHour(0);
        } else {
            beginDate = ISODateTimeFormat.date().parseLocalDate(start).toDateTimeAtStartOfDay();
            endDate = ISODateTimeFormat.date().parseLocalDate(end).plusDays(1).toDateTimeAtStartOfDay().minusMillis(1);
        }
        return new Interval(beginDate, endDate);
    }

    private JsonArray toJson(Iterable<ScheduleEventBean> events) {
        JsonArray array = new JsonArray();

        events.forEach(event -> {
            JsonObject ev = new JsonObject();
            ev.addProperty("id", event.id);
            ev.addProperty("start", event.begin.getMillis());
            ev.addProperty("end", event.end.getMillis());
            ev.addProperty("url", event.url);
            ev.addProperty("title", event.getTitle());
            ev.addProperty("description", event.getDescription());
            ev.addProperty("color", event.color);
            array.add(ev);
        });

        return array;
    }

    private Collection<ScheduleEventBean> allPublicEvaluations(Degree degree, Interval interval) {
        Set<ScheduleEventBean> allEvaluations = new HashSet<>(writtenEvaluations(degree, interval));
        allEvaluations.addAll(projects(degree, interval));
        return allEvaluations;
    }

    private Collection<ScheduleEventBean> writtenEvaluations(Degree degree, Interval interval) {
        return allExecutionCourses(degree)
                .flatMap(executionCourse -> executionCourse.getAssociatedWrittenEvaluations().stream())
                .filter(writtenEval -> writtenEval.getBeginningDateTime() != null
                        && interval.contains(writtenEval.getBeginningDateTime())).map(this::createEventBean)
                .collect(Collectors.toSet());
    }

    private ScheduleEventBean createEventBean(WrittenEvaluation evaluation) {
        ExecutionCourse ec = evaluation.getAssociatedExecutionCoursesSet().stream().findFirst().get();
        return new ScheduleEventBean(ec.getPrettyAcronym(), evaluation.getEvaluationType().toString(), evaluation.getFullName(),
                evaluation.getBeginningDateTime(), evaluation.getEndDateTime(), null, null,
                colorForType(evaluation.getEvaluationType()), null, null);
    }

    private Collection<ScheduleEventBean> projects(Degree degree, Interval interval) {
        Set<ScheduleEventBean> projects = Sets.newHashSet();
        allExecutionCourses(degree).forEach(executionCourse -> {
            for (Project project : executionCourse.getAssociatedProjects()) {
                projects.addAll(projectEvents(project, executionCourse, interval));
            }
        });
        return projects;
    }

    private static Collection<ScheduleEventBean> projectEvents(Project project, ExecutionCourse executionCourse, Interval interval) {
        DateTime projectStart = project.getProjectBeginDateTime();
        DateTime projectEnd = project.getProjectEndDateTime();

        Set<ScheduleEventBean> events = new HashSet<>();

        if (interval.contains(projectStart)) {
            events.add(new ScheduleEventBean(executionCourse.getPrettyAcronym(), project.getEvaluationType().toString(), project
                    .getPresentationName(), projectStart, projectStart.plusHours(1), null, null, colorForType(project
                    .getEvaluationType()), null, null));
        }
        if (interval.contains(projectEnd)) {
            events.add(new ScheduleEventBean(executionCourse.getPrettyAcronym(), project.getEvaluationType().toString(), project
                    .getPresentationName(), projectEnd.minusHours(1), projectEnd, null, null, colorForType(project
                    .getEvaluationType()), null, null));
        }

        return events;
    }

    private static String colorForType(EvaluationType type) {
        return ScheduleEventBean.COLORS[type.getType() % ScheduleEventBean.COLORS.length];
    }

    private Stream<ExecutionCourse> allExecutionCourses(Degree degree) {
        return degree.getDegreeCurricularPlansSet().stream()
                .flatMap(curricularPlan -> curricularPlan.getCurricularCoursesSet().stream())
                .flatMap(curricularCourse -> curricularCourse.getAssociatedExecutionCoursesSet().stream());
    }

    private boolean hasPermissionToViewSchedule(ExecutionCourse executionCourse) {
        boolean isOpenPeriod = !executionCourse.getExecutionPeriod().isNotOpen();
        boolean isLogged = Authenticate.isLogged();
        boolean isAllocationManager = DynamicGroup.get("resourceAllocationManager").isMember(Authenticate.getUser());
        boolean isCoordinator =
                executionCourse.getDegreesSortedByDegreeName().stream()
                        .flatMap(degree -> degree.getCurrentCoordinators().stream()).map(Coordinator::getPerson)
                        .filter(coordinator -> coordinator.equals(Authenticate.getUser().getPerson())).findFirst().isPresent();
        return isOpenPeriod || (isLogged && (isAllocationManager || isCoordinator));
    }
}
