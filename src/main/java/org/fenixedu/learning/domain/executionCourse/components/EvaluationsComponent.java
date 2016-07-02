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

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;

import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.Exam;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Project;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

@ComponentType(name = "Evaluations", description = "Evaluations for an Execution Course")
public class EvaluationsComponent extends BaseExecutionCourseComponent {

    private static final Comparator<Evaluation> EVALUATION_COMPARATOR = Comparator.comparing(Evaluation::getEvaluationDate);
    private static final Comparator<Project> PROJECT_COMPARATOR =
            Comparator.comparing(Project::getProjectBeginDateTime).thenComparing(Project::getProjectEndDateTime)
                    .thenComparing(Project::getName);

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("comment", executionCourse.getComment());
        globalContext.put("adHocEvaluations", executionCourse.getOrderedAssociatedAdHocEvaluations());
        globalContext.put("projects",
                executionCourse.getAssociatedProjects().stream().sorted(PROJECT_COMPARATOR).collect(toList()));
        globalContext.put("publishedExams", publishedExams(executionCourse));
        globalContext.put("writtenTests",
                executionCourse.getAssociatedWrittenTests().stream().sorted(EVALUATION_COMPARATOR).collect(toList()));
    }

    private List<Exam> publishedExams(ExecutionCourse executionCourse) {
        return executionCourse.getAssociatedExams().stream().filter(Exam::isExamsMapPublished).sorted(EVALUATION_COMPARATOR)
                .collect(toList());
    }

}
