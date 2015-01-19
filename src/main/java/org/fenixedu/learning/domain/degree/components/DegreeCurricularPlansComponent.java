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

import java.util.Optional;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenixframework.FenixFramework;

/**
 * Created by borgez on 10/8/14.
 */
@ComponentType(name = "Degree curricular plans", description = "Curricular plans of a degree")
public class DegreeCurricularPlansComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Degree degree = degree(page);

        Optional<ExecutionYear> executionYear = getSelectedExecutionYear(globalContext.getRequestContext());
        if (executionYear.isPresent()) {
            globalContext.put("degreeCurricularPlan", degree.getDegreeCurricularPlansForYear(executionYear.get()).stream()
                    .findFirst().get());
            globalContext.put("executionYear", executionYear.get());
        } else {
            DegreeCurricularPlan degreeCurricularPlan = degree.getMostRecentDegreeCurricularPlan();
            globalContext.put("degreeCurricularPlan", degree.getMostRecentDegreeCurricularPlan());
            globalContext.put("executionYear", degreeCurricularPlan.getMostRecentExecutionYear());
        }
        globalContext.put("executionYears", degree.getDegreeCurricularPlansExecutionYears());
    }

    private Optional<ExecutionYear> getSelectedExecutionYear(String[] requestContext) {
        return requestContext.length > 2 ? Optional.of(FenixFramework.getDomainObject(requestContext[1])) : Optional.empty();
    }
}
