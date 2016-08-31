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

import java.util.Map;
import java.util.TreeMap;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Mark;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "Marks", description = "Marks for an Execution Course")
public class MarksComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = page.getSite().getExecutionCourse();
        Map<Attends, Map<Evaluation, Mark>> attendsMap = attendsMap(executionCourse);
        globalContext.put("attendsMap", attendsMap);
        globalContext.put("numberOfStudents", attendsMap.size());
        globalContext.put("dont-cache-pages-in-search-engines", Boolean.TRUE);
        globalContext.put("evaluations", executionCourse.getOrderedAssociatedEvaluations());
    }

    private Map<Attends, Map<Evaluation, Mark>> attendsMap(ExecutionCourse executionCourse) {
        final Map<Attends, Map<Evaluation, Mark>> attendsMap =
                new TreeMap<Attends, Map<Evaluation, Mark>>(Attends.COMPARATOR_BY_STUDENT_NUMBER);
        for (final Attends attends : executionCourse.getAttendsSet()) {
            final Map<Evaluation, Mark> evaluationsMap = new TreeMap<Evaluation, Mark>(ExecutionCourse.EVALUATION_COMPARATOR);
            attendsMap.put(attends, evaluationsMap);
            for (final Evaluation evaluation : executionCourse.getAssociatedEvaluationsSet()) {
                if (evaluation.getPublishmentMessage() != null) {
                    evaluationsMap.put(evaluation, null);
                }
            }
            for (final Mark mark : attends.getAssociatedMarksSet()) {
                if (mark.getEvaluation().getPublishmentMessage() != null) {
                    evaluationsMap.put(mark.getEvaluation(), mark);
                }
            }
        }
        return attendsMap;
    }
}
