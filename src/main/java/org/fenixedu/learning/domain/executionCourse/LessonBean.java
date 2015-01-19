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
package org.fenixedu.learning.domain.executionCourse;

import java.util.Calendar;
import java.util.TimeZone;

import org.fenixedu.academic.dto.InfoLessonInstanceAggregation;

import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.ComparisonChain;

public class LessonBean implements Comparable<LessonBean> {
    private static final String[] COLORS = new String[] { "CCAE87", "997649", "FFE8E0", "BECC87", "#FF9999", "#FFCC99",
            "#FFFF99", "#CCFF99", "#99FF99", "#99FF88" };

    private final InfoLessonInstanceAggregation info;

    public LessonBean(InfoLessonInstanceAggregation info) {
        this.info = info;
    }

    public String getEnd() {
        return isoDate(getWeekDay(), getEndHour(), getEndMinutes(), 0);
    }

    public String getStart() {
        return isoDate(getWeekDay(), getBeginHour(), getBeginMinutes(), 0);
    }

    private String isoDate(int dayOfWeek, int hour, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        return ISODateTimeFormat.dateTime().print(calendar.getTime().getTime());
    }

    public String getId() {
        return info.getExternalId();
    }

    public int getWeekDay() {
        return info.getDiaSemana().getDiaSemana();
    }

    public int getBeginHour() {
        return info.getBeginHourMinuteSecond().getHour();
    }

    public int getBeginMinutes() {
        return info.getBeginHourMinuteSecond().getMinuteOfHour();
    }

    public int getEndHour() {
        return info.getEndHourMinuteSecond().getHour();
    }

    public int getEndMinutes() {
        return info.getEndHourMinuteSecond().getMinuteOfHour();
    }

    public String getShiftType() {
        return info.getShift().getShiftTypesPrettyPrint();
    }

    public String getShiftTypeCode() {
        return info.getShift().getShiftTypesCodePrettyPrint();
    }

    public String getShiftSpace() {
        return info.getAllocatableSpace().getName();
    }

    public String getShiftWeeks() {
        return info.prettyPrintWeeks();
    }

    public InfoLessonInstanceAggregation getInfo() {
        return info;
    }

    public String getTextColor() {
        return "#333";
    }

    public String getSpaceUrl() {
        return "#";
    }

    public String getColor() {
        int id = info.getShift().getTypes().stream().findFirst().get().ordinal();
        return COLORS[id % COLORS.length];
    }

    @Override
    public int compareTo(LessonBean o) {
        return ComparisonChain.start().compare(this.getWeekDay(), o.getWeekDay()).compare(this.getBeginHour(), o.getBeginHour())
                .compare(this.getBeginMinutes(), o.getBeginMinutes()).result();
    }

}
