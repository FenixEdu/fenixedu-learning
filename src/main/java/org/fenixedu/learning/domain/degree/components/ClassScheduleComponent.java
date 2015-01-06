package org.fenixedu.learning.domain.degree.components;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.ScheduleEventBean;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.Interval;

/**
 * Created by borgez on 10/9/14.
 */
@ComponentType(name = "Class Schedule", description = "Info about the class schedule of a degree")
public class ClassScheduleComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        SchoolClass schoolClass = getDomainObject(globalContext.getRequestContext()[1]);
        globalContext.put("defaultView", "agendaWeek");
        globalContext.put("events", getEvents(schoolClass));
    }

    private Collection<ScheduleEventBean> getEvents(SchoolClass schoolClass) {
        return schoolClass.getAssociatedShiftsSet().stream().flatMap(shift -> shift.getAssociatedLessonsSet().stream())
                .flatMap(lesson -> lessonEvents(lesson)).collect(toList());
    }

    private Stream<ScheduleEventBean> lessonEvents(Lesson lesson) {
        return Stream.concat(lessonsWithoutInstances(lesson), lessonWithInstances(lesson));
    }

    private Stream<ScheduleEventBean> lessonsWithoutInstances(Lesson lesson) {
        return lesson.getAllLessonIntervalsWithoutInstanceDates().stream().map(interval -> createEventBean(lesson, interval));
    }

    private Stream<ScheduleEventBean> lessonWithInstances(Lesson lesson) {
        return lesson.getLessonInstancesSet().stream().map(instance -> createEventBean(lesson, instance.getInterval()));
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
}
