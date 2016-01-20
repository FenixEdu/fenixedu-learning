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

import java.util.Locale;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.commons.i18n.LocalizedString;

@ComponentType(name = "EvaluationMethods", description = "Evaluation Methods for an Execution Course")
public class EvaluationMethodsComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse =page.getSite().getExecutionCourse();
        LocalizedString evaluationMethod = getEvaluationMethod(executionCourse);
        globalContext.put("evaluationMethod", executionCourse.getEvaluationMethod());
        globalContext.put("evaluationMethodText", evaluationMethod.getContent());
        globalContext.put("evaluationMethodLocalizedString", evaluationMethod);
    }

    private LocalizedString getEvaluationMethod(ExecutionCourse executionCourse) {
        if (executionCourse.getEvaluationMethod() != null) {
            return executionCourse.getEvaluationMethod().getEvaluationElements().toLocalizedString();
        } else {
            String competenceMethod =
                    !executionCourse.getCompetenceCourses().isEmpty() ? executionCourse.getCompetenceCourses().iterator().next()
                            .getEvaluationMethod(executionCourse.getExecutionPeriod()) : "";
            return new LocalizedString(Locale.getDefault(), competenceMethod);
        }
    }
}
