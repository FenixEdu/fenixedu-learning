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

import static com.google.common.collect.ImmutableMap.of;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static org.fenixedu.academic.domain.ExecutionSemester.readActualExecutionSemester;
import static org.fenixedu.academic.domain.SchoolClass.COMPARATOR_BY_NAME;
import static org.fenixedu.academic.util.PeriodState.NOT_OPEN;
import static org.fenixedu.academic.util.PeriodState.OPEN;
import static org.fenixedu.bennu.core.security.Authenticate.getUser;
import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.academic.dto.InfoDegree;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import com.google.common.collect.Sets;

/**
 * Created by borgez on 10/9/14.
 */
@ComponentType(name = "Degree classes", description = "Schedule of a class")
public class DegreeClassesComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext global) {
        Degree degree = degree(page);
        global.put("degreeInfo", InfoDegree.newInfoFromDomain(degree));
        global.put("timetablePage",
                pageForComponent(page.getSite(), ClassScheduleComponent.class).map(Page::getAddress).orElse(null));

        ExecutionSemester selectedSemester = getExecutionSemester(global.getRequestContext());
        ExecutionSemester otherSemester = getOtherExecutionSemester(selectedSemester);

        SortedMap<Integer, Set<SchoolClass>> selectedClasses = classesByCurricularYear(degree, selectedSemester);
        SortedMap<Integer, Set<SchoolClass>> nextClasses = classesByCurricularYear(degree, otherSemester);
        global.put("classesByCurricularYearAndSemesters", of(selectedSemester, selectedClasses, otherSemester, nextClasses));
    }

    private SortedMap<Integer, Set<SchoolClass>> classesByCurricularYear(Degree degree, ExecutionSemester semester) {
		DegreeCurricularPlan plan = degree.getDegreeCurricularPlansForYear(semester.getExecutionYear()).stream()
				.findFirst().orElse(degree.getMostRecentDegreeCurricularPlan());
		Predicate<SchoolClass> predicate = schoolClass -> schoolClass.getExecutionDegree().getDegreeCurricularPlan() == plan;
        return semester
                .getSchoolClassesSet()
                .stream()
                .filter(predicate)
                .collect(
                        groupingBy(SchoolClass::getAnoCurricular, TreeMap::new,
                                toCollection(() -> Sets.newTreeSet(COMPARATOR_BY_NAME))));
    }

    private ExecutionSemester getOtherExecutionSemester(ExecutionSemester semester) {
        ExecutionSemester next = semester.getNextExecutionPeriod();
        return next != null && canViewNextExecutionSemester(next) ? next : semester.getPreviousExecutionPeriod();
    }

    private boolean canViewNextExecutionSemester(ExecutionSemester nextExecutionSemester) {
        Predicate<Person> hasPerson = person -> person != null;
        Predicate<Person> isCoordinator = person -> RoleType.COORDINATOR.isMember(person.getUser());
        Predicate<Person> isManager = person -> RoleType.RESOURCE_ALLOCATION_MANAGER.isMember(person.getUser());
        return nextExecutionSemester.getState() == OPEN
                || (nextExecutionSemester.getState() == NOT_OPEN && getUser() != null && hasPerson.and(
                        isManager.or(isCoordinator)).test(getUser().getPerson()));
    }

    private ExecutionSemester getExecutionSemester(String[] requestContext) {
        return requestContext.length > 2 ? getDomainObject(requestContext[1]) : readActualExecutionSemester();
    }
}
