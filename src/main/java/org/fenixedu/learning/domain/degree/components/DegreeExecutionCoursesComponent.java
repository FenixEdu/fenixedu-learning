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
package org.fenixedu.learning.domain.degree.components;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static org.fenixedu.academic.domain.ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR;
import static org.fenixedu.academic.dto.ExecutionCourseView.COMPARATOR_BY_NAME;

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.dto.ExecutionCourseView;
import org.fenixedu.academic.util.PeriodState;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Created by borgez on 10/8/14.
 */
@ComponentType(name = "Degree execution courses", description = "execution courses for a degree")
public class DegreeExecutionCoursesComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Degree degree = degree(page);
        globalContext.put("executionCoursesBySemesterAndCurricularYear", executionCourses(degree));
        globalContext.put("executionYears", degree.getDegreeCurricularPlansExecutionYears());
    }

    public SortedMap<ExecutionSemester, SortedMap<Integer, SortedSet<ExecutionCourseView>>> executionCourses(final Degree degree) {
        TreeMap<ExecutionSemester, SortedMap<Integer, SortedSet<ExecutionCourseView>>> result =
                Maps.newTreeMap(COMPARATOR_BY_SEMESTER_AND_YEAR);

        ExecutionSemester currentExecutionPeriod = ExecutionSemester.readActualExecutionSemester();
        ExecutionSemester previousExecutionPeriod = currentExecutionPeriod.getPreviousExecutionPeriod();
        ExecutionSemester nextExecutionSemester = currentExecutionPeriod.getNextExecutionPeriod();
        boolean hasNextExecutionSemester =
                nextExecutionSemester != null && nextExecutionSemester.getState().equals(PeriodState.OPEN);
        ExecutionSemester selectedExecutionPeriod = hasNextExecutionSemester ? nextExecutionSemester : previousExecutionPeriod;

        result.put(selectedExecutionPeriod, executionCourses(degree, selectedExecutionPeriod));
        result.put(currentExecutionPeriod, executionCourses(degree, currentExecutionPeriod));
        return result;
    }

    public SortedMap<Integer, SortedSet<ExecutionCourseView>> executionCourses(Degree degree, ExecutionSemester executionSemester) {
        Set<ExecutionCourseView> executionCoursesViews = Sets.newHashSet();
        degree.getActiveDegreeCurricularPlans().forEach(
                plan -> plan.addExecutionCourses(executionCoursesViews, executionSemester));
        return executionCoursesViews.stream().collect(
                groupingBy(ExecutionCourseView::getCurricularYear, TreeMap::new, toCollection(factory)));
    }

    private static final Supplier<SortedSet<ExecutionCourseView>> factory = () -> Sets.newTreeSet(COMPARATOR_BY_NAME);

}
