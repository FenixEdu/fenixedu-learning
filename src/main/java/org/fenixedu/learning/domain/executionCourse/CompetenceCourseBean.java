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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Objects;

public class CompetenceCourseBean {
    private final CompetenceCourse competenceCourse;
    private final ExecutionSemester executionSemester;
    private final Set<CurricularCourse> curricularCourses;
    private final LocalizedString name;
    private final LocalizedString objectives;
    private final LocalizedString program;

    public CompetenceCourseBean(CompetenceCourse competenceCourse, Set<CurricularCourse> curricularCourses,
            ExecutionSemester executionSemester) {
        this.competenceCourse = competenceCourse;
        this.executionSemester = executionSemester;
        this.curricularCourses = curricularCourses;
        this.name = competenceCourse.getNameI18N(executionSemester).toLocalizedString();
        this.objectives = competenceCourse.getObjectivesI18N(executionSemester).toLocalizedString();
        this.program = competenceCourse.getProgramI18N(executionSemester).toLocalizedString();
    }

    public CompetenceCourse getCompetenceCourse() {
        return competenceCourse;
    }

    public ExecutionSemester getExecutionSemester() {
        return executionSemester;
    }

    public Set<CurricularCourse> getCurricularCourses() {
        return curricularCourses;
    }

    public LocalizedString getName() {
        return name;
    }

    public LocalizedString getObjectives() {
        return objectives;
    }

    public static List<CompetenceCourseBean> approvedCompetenceCourses(ExecutionCourse executionCourse) {
        return executionCourse.getCurricularCoursesIndexedByCompetenceCourse().entrySet().stream()
                .filter(entry -> entry.getKey().isApproved())
                .map(entry -> new CompetenceCourseBean(entry.getKey(), entry.getValue(), executionCourse.getExecutionPeriod()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", this.name).add("objectives", this.objectives)
                .add("executionSemester", executionSemester).add("curricularCourses", curricularCourses).toString();
    }

    public LocalizedString getProgram() {
        return program;
    }
}