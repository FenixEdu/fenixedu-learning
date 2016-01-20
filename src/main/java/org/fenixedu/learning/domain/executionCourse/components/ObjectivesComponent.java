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
package org.fenixedu.learning.domain.executionCourse.components;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Curriculum;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.CompetenceCourseBean;

@ComponentType(name = "CompetenceCourse", description = "Competence Course information for an Execution Course")
public class ObjectivesComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = page.getSite().getExecutionCourse();
        globalContext.put("executionPeriod", executionCourse.getExecutionPeriod());
        globalContext.put("competenceCourseBeans", CompetenceCourseBean.approvedCompetenceCourses(executionCourse));
        globalContext.put("curriculumByCurricularCourse", curriculumsByCurricularCourses(executionCourse));
    }

    private Map<CurricularCourse, Curriculum> curriculumsByCurricularCourses(ExecutionCourse executionCourse) {
        Date end = executionCourse.getExecutionPeriod().getExecutionYear().getEndDate();
        return executionCourse.getCurricularCoursesSortedByDegreeAndCurricularCourseName().stream()
                .filter(curricularCourse -> !curricularCourse.isBolonhaDegree())
                .map(curricularCourse -> curricularCourse.findLatestCurriculumModifiedBefore(end)).filter(Objects::nonNull)
                .collect(Collectors.toMap(Curriculum::getCurricularCourse, Function.identity()));
    }
}
