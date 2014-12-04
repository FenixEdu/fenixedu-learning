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
