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

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.fenixedu.academic.domain.ExecutionCourse.EXECUTION_COURSE_EXECUTION_PERIOD_COMPARATOR;

@ComponentType(name = "InitialPage", description = "Provides the information needed for the initial page of an Execution Course")
public class InitialPageComponent extends BaseExecutionCourseComponent {
    public final static int ANNOUNCEMENTS_TO_SHOW = 5;

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put(
                "professorships",
                executionCourse
                        .getProfessorshipsSet()
                        .stream()
                        .sorted(Comparator.comparing(Professorship::isResponsibleFor).reversed()
                                .thenComparing(Professorship.COMPARATOR_BY_PERSON_NAME)).collect(Collectors.toList()));
        globalContext.put("isStudent", isStudent(Authenticate.getUser()));
        globalContext.put("executionCourse", executionCourse);
        globalContext.put("previousExecutionCourses", previousExecutionCourses(executionCourse));
    }

    private List<ExecutionCourse> previousExecutionCourses(ExecutionCourse executionCourse) {
        return executionCourse.getAssociatedCurricularCoursesSet().stream()
                .flatMap(c -> c.getAssociatedExecutionCoursesSet().stream()).distinct().filter(e -> !executionCourse.equals(e))
                .sorted(EXECUTION_COURSE_EXECUTION_PERIOD_COMPARATOR.reversed()).collect(toList());
    }

    private boolean isStudent(User user) {
        return user != null && user.getPerson() != null && user.getPerson().getStudent() != null;
    }
}
