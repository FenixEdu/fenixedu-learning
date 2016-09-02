/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Learning.
 *
 * FenixEdu Learning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Learning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Learning.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.learning.api;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.fenixedu.academic.domain.*;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.util.EvaluationType;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.learning.domain.ScheduleEventBean;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import javax.ws.rs.*;
import java.util.*;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.fenixedu.academic.domain.ExecutionYear.COMPARATOR_BY_YEAR;

@Path("/fenixedu-learning/events")
public class EventsResource {

    private static Collection<ScheduleEventBean> projectEvents(Project project, ExecutionCourse executionCourse,
            Interval interval) {
        DateTime projectStart = project.getProjectBeginDateTime();
        DateTime projectEnd = project.getProjectEndDateTime();

        Set<ScheduleEventBean> events = new HashSet<>();

        if (interval.contains(projectStart)) {
            events.add(new ScheduleEventBean(executionCourse.getPrettyAcronym(), project.getEvaluationType().toString(), project
                    .getPresentationName(), projectStart, projectStart.plusHours(1), null, executionCourse.getSiteUrl(),
                    colorForType(project.getEvaluationType()), null, null));
        }
        if (interval.contains(projectEnd)) {
            events.add(new ScheduleEventBean(executionCourse.getPrettyAcronym(), project.getEvaluationType().toString(), project
                    .getPresentationName(), projectEnd.minusHours(1), projectEnd, null, executionCourse.getSiteUrl(),
                    colorForType(project.getEvaluationType()), null, null));
        }

        return events;
    }

    private static String colorForType(EvaluationType type) {
        return ScheduleEventBean.COLORS[type.getType() % ScheduleEventBean.COLORS.length];
    }

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
    @Path("/executionCourse/{course}/nearestEvent")
    @Produces("application/json; charset=utf-8")
    public String nearestExecutionCourseEvent(@PathParam("course") ExecutionCourse course) {
        LocalDate date = LocalDate.now();
        if (hasPermissionToViewSchedule(course)) {
            date = nearestEventDate(ScheduleEventBean.forExecutionCourse(course, course.getAcademicInterval().toInterval()));
        }
        return toJson(date).toString();
    }

    @GET
    @Path("/degree/evaluations/{degree}")
    @Produces("application/json; charset=utf-8")
    public String degreeEvaluationsEvents(@PathParam("degree") Degree degree, @QueryParam("start") String start,
            @QueryParam("end") String end) {
        return toJson(allPublicEvaluations(degree, getInterval(start, end))).toString();
    }

    @GET
    @Path("/degree/class/{schoolClass}/nearestEvent")
    @Produces("application/json; charset=utf-8")
    public String nearestClassScheduleEvent(@PathParam("schoolClass") SchoolClass schoolClass) {
        return toJson(nearestEventDate(getEvents(schoolClass, schoolClass.getAcademicInterval().toInterval()))).toString();
    }

    @GET
    @Path("/degree/class/{schoolClass}")
    @Produces("application/json; charset=utf-8")
    public String classScheduleEvents(@PathParam("schoolClass") SchoolClass schoolClass, @QueryParam("start") String start,
            @QueryParam("end") String end) {
        return toJson(getEvents(schoolClass, getInterval(start, end))).toString();
    }

    @GET
    @Path("/degree/evaluations/{degree}/nearestEvent")
    @Produces("application/json; charset=utf-8")
    public String nearestEvaluationEvent(@PathParam("degree") Degree degree) {
        Optional<Interval> interval =
                degree.getDegreeCurricularPlansExecutionYears().stream().sorted(COMPARATOR_BY_YEAR.reversed())
                        .map(year -> year.getAcademicInterval().toInterval()).findFirst();
        LocalDate date = interval.isPresent() ? nearestEventDate(allPublicEvaluations(degree, interval.get())) : LocalDate.now();
        return toJson(date).toString();
    }

    private Collection<ScheduleEventBean> getEvents(SchoolClass schoolClass, Interval interval) {
        return schoolClass.getAssociatedShiftsSet().stream().flatMap(shift -> shift.getAssociatedLessonsSet().stream())
                .flatMap(lesson -> lessonEvents(lesson, interval)).collect(toList());
    }

    private Stream<ScheduleEventBean> lessonEvents(Lesson lesson, Interval interval) {
        return Stream.concat(lessonsWithoutInstances(lesson, interval), lessonWithInstances(lesson, interval));
    }

    private Stream<ScheduleEventBean> lessonsWithoutInstances(Lesson lesson, Interval interval) {
        return lesson.getAllLessonIntervalsWithoutInstanceDates().stream()
                .filter(i -> interval.contains(i) || i.contains(interval)).map(i -> createEventBean(lesson, i));
    }

    private Stream<ScheduleEventBean> lessonWithInstances(Lesson lesson, Interval interval) {
        return lesson.getLessonInstancesSet().stream().filter(instance -> interval.contains(instance.getInterval()))
                .map(instance -> createEventBean(lesson, instance.getInterval()));
    }

    private ScheduleEventBean createEventBean(Lesson lesson, Interval interval) {
        Optional<Site> site = Optional.ofNullable(lesson.getExecutionCourse().getSite());
        String url = site.isPresent() ? site.get().getFullUrl() : "#";
        String executionCourseAcronym = lesson.getShift().getExecutionCourse().getPrettyAcronym();
        String shiftTypeAcronym = lesson.getShift().getShiftTypesCodePrettyPrint();
        String executionCourseName = lesson.getShift().getExecutionCourse().getNameI18N().getContent();
        String shifType = lesson.getShift().getShiftTypesPrettyPrint();
        Set<Space> location =
                lesson.getLessonSpaceOccupation() != null ? lesson.getLessonSpaceOccupation().getSpaces() : newHashSet();
        String description = executionCourseName + "( " + shifType + " )";
        return new ScheduleEventBean(executionCourseAcronym, shiftTypeAcronym, description, interval.getStart(),
                interval.getEnd(), null, url, null, null, location);
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

            ev.addProperty("start", event.begin.toDateTimeISO().toString());
            ev.addProperty("end", event.end.toDateTimeISO().toString());
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
        Set<ScheduleEventBean> events = new HashSet<>();
        allExecutionCourses(degree).forEach(
                executionCourse -> executionCourse
                        .getAssociatedWrittenEvaluations()
                        .stream()
                        .filter(writtenEval -> writtenEval.getBeginningDateTime() != null
                                && interval.contains(writtenEval.getBeginningDateTime()))
                        .forEach(writtenEval -> events.add(createEventBean(executionCourse, writtenEval))));
        return events;
    }

    private ScheduleEventBean createEventBean(ExecutionCourse executionCourse, WrittenEvaluation evaluation) {
        return new ScheduleEventBean(executionCourse.getPrettyAcronym(), evaluation.getEvaluationType().toString(),
                executionCourse.getName(), evaluation.getBeginningDateTime(), evaluation.getEndDateTime(), null,
                executionCourse.getSiteUrl(), colorForType(evaluation.getEvaluationType()), null, null);
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

    private Stream<ExecutionCourse> allExecutionCourses(Degree degree) {
        return degree.getDegreeCurricularPlansSet().stream()
                .flatMap(curricularPlan -> curricularPlan.getCurricularCoursesSet().stream())
                .flatMap(curricularCourse -> curricularCourse.getAssociatedExecutionCoursesSet().stream());
    }

    private boolean hasPermissionToViewSchedule(ExecutionCourse executionCourse) {
        boolean isOpenPeriod = !executionCourse.getExecutionPeriod().isNotOpen();
        boolean isLogged = Authenticate.isLogged();
        boolean isAllocationManager = isLogged && DynamicGroup.get("resourceAllocationManager").isMember(Authenticate.getUser());
        boolean isCoordinator =
                executionCourse.getDegreesSortedByDegreeName().stream()
                        .flatMap(degree -> degree.getCurrentCoordinators().stream()).map(Coordinator::getPerson)
                        .filter(coordinator -> coordinator.equals(AccessControl.getPerson())).findFirst().isPresent();
        return isOpenPeriod || (isLogged && (isAllocationManager || isCoordinator));
    }

    private JsonElement toJson(LocalDate localDate) {
        if (localDate == null) {
            return new JsonPrimitive(ISODateTimeFormat.date().print(LocalDate.now()));
        } else {
            return new JsonPrimitive(ISODateTimeFormat.date().print(localDate));
        }
    }

    private LocalDate nearestEventDate(Collection<ScheduleEventBean> events) {
        LocalDate now = LocalDate.now();
        LocalDate result =
                events.stream().map(e -> e.begin).map(DateTime::toLocalDate).collect(toCollection(TreeSet::new)).lower(now);
        return Optional.ofNullable(result).orElse(now);
    }
}
