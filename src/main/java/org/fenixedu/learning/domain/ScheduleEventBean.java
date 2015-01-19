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
package org.fenixedu.learning.domain;

import static org.fenixedu.commons.i18n.I18N.getLocale;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CourseLoad;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Created by borgez on 10/14/14.
 */
public class ScheduleEventBean implements Comparable<ScheduleEventBean> {
    public static final String[] COLORS = new String[] { "CCAE87", "997649", "FFE8E0", "BECC87", "#FF9999", "#FFCC99", "#FFFF99",
            "#CCFF99", "#99FF99", "#99FF88" };
    private static final String COLOR_BLACK = "333";

    public final LocalizedString title;
    public final LocalizedString subtitle;
    public final LocalizedString description;
    public final DateTime begin;
    public final DateTime end;
    public final String id;
    public final String url;
    public final String color;
    public final String textColor;
    public final Collection<Space> spaces;
    public final String location;

    public ScheduleEventBean(LocalizedString title, LocalizedString subtitle, LocalizedString description, DateTime start,
            DateTime end, String id, String url, String color, String textColor, Collection<Space> spaces) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.begin = start.toDateTimeISO();
        this.end = Optional.ofNullable(end).orElse(start).toDateTimeISO();
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID().toString());
        this.url = Optional.ofNullable(url).orElse("#");
        this.color = Optional.ofNullable(color).orElse(COLORS[0]);
        this.textColor = Optional.ofNullable(color).orElse(COLOR_BLACK);
        this.spaces = Optional.ofNullable(spaces).orElse(ImmutableSet.of());
        this.location = secureSpaceNames(spaces);
    }

    public ScheduleEventBean(String title, String subtitle, String description, DateTime start, DateTime end, String id,
            String url, String color, String textColor, Collection<Space> spaces) {
        this(new LocalizedString().with(getLocale(), title), new LocalizedString().with(getLocale(), subtitle),
                new LocalizedString().with(getLocale(), description), start, end, id, url, color, textColor, spaces);
    }

    private String secureSpaceNames(Collection<Space> spaces) {
        try {
            return spaces.stream().map(Space::getPresentationName).collect(Collectors.joining(";"));
        } catch (Exception e) {
            return new String();
        }
    }

    @Override
    public int compareTo(ScheduleEventBean o) {
        return begin.compareTo(o.begin);
    }

    public static Collection<ScheduleEventBean> forExecutionCourse(ExecutionCourse executionCourse, Interval interval) {
        List<ScheduleEventBean> events = Lists.newArrayList();
        for (CourseLoad courseLoad : executionCourse.getCourseLoadsSet()) {
            for (Shift shift : courseLoad.getShiftsSet()) {
                for (Lesson lesson : shift.getAssociatedLessonsSet()) {
                    for (Interval lessonInterval : lesson.getAllLessonIntervals()) {
                        if (interval.contains(lessonInterval)) {
                            events.add(scheduleEvent(shift, lesson, lessonInterval));
                        }
                    }
                }
            }
        }
        return events;
    }

    public String getTitle() {
        return Optional.ofNullable(title.getContent()).orElse("") + "\n" + Optional.ofNullable(subtitle.getContent()).orElse("");
    }

    public String getDescription() {
        return Optional.ofNullable(description.getContent()).orElse("") + "\n" + Optional.ofNullable(location).orElse("");
    }

    private static ScheduleEventBean scheduleEvent(Shift shift, Lesson lesson, Interval interval) {
        String shiftAcronym = shift.getShiftTypesCodePrettyPrint();
        String url = lesson.getLessonSpaceOccupation() != null ? lesson.getLessonSpaceOccupation().getUrl() : null;
        String roomName = lesson.getSala() != null ? lesson.getSala().getName() : null;
        String shiftTypesPrettyPrint = shift.getShiftTypesPrettyPrint();
        String color = ScheduleEventBean.COLORS[shift.getSortedTypes().stream().findFirst().get().ordinal()];
        HashSet<Space> spaces = Sets.newHashSet(lesson.getSala());
        return new ScheduleEventBean(shiftAcronym, roomName, shiftTypesPrettyPrint, interval.getStart(), interval.getEnd(), null,
                url, color, null, spaces);
    }
}
