package org.fenixedu.learning.domain.degree.components;

import static java.util.stream.Collectors.toList;
import static org.fenixedu.academic.domain.ExecutionCourse.EXECUTION_COURSE_EXECUTION_PERIOD_COMPARATOR;
import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import java.util.HashMap;
import java.util.Map;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import com.google.common.collect.Maps;

/**
 * Created by borgez on 10/15/14.
 */
@ComponentType(name = "Curricular Course", description = "Curricular course info")
public class CurricularCourseComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        CurricularCourse curricularCourse = getDomainObject(globalContext.getRequestContext()[1]);
        ExecutionSemester period = getExecutionSemester(curricularCourse, globalContext.getRequestContext());
        globalContext.put("curricularCourse", createWrap(curricularCourse, period));
    }

    private ExecutionSemester getExecutionSemester(CurricularCourse curricularCourse, String[] request) {
        return request.length > 2 ? getDomainObject(request[2]) : curricularCourse.getParentDegreeCurricularPlan()
                .getFirstExecutionDegree().getExecutionYear().getFirstExecutionPeriod();
    }

    private HashMap<String, Object> createWrap(CurricularCourse curricularCourse, ExecutionSemester period) {
        HashMap<String, Object> wrap = Maps.newHashMap();
        wrap.put("period", period);
        wrap.put("name", curricularCourse.getNameI18N(period).toLocalizedString());
        wrap.put("degreeCurricularPlanName", curricularCourse.getDegreeCurricularPlan().getPresentationName());
        wrap.put("acronym", curricularCourse.getAcronym(period));
        wrap.put("isOptional", curricularCourse.isOptional());
        wrap.put("executionCourses",
                curricularCourse.getAssociatedExecutionCoursesSet().stream().sorted(EXECUTION_COURSE_EXECUTION_PERIOD_COMPARATOR)
                        .limit(5).map(ec -> createWrap(ec)).collect(toList()));
        wrap.put("parentContexts", curricularCourse.getParentContextsByExecutionYear(period.getExecutionYear()));
        wrap.put("weight", curricularCourse.getWeight(period));
        wrap.put("prerequisites", curricularCourse.getPrerequisitesI18N().toLocalizedString());
        wrap.put("objectives", curricularCourse.getObjectivesI18N(period).toLocalizedString());
        wrap.put("program", curricularCourse.getProgramI18N(period).toLocalizedString());
        wrap.put("evaluationMethod", curricularCourse.getEvaluationMethodI18N(period).toLocalizedString());
        return wrap;
    }

    private Map<String, Object> createWrap(ExecutionCourse executionCourse) {
        Map<String, Object> wrap = Maps.newHashMap();
        wrap.put("name", executionCourse.getNameI18N().toLocalizedString());
        wrap.put("executionYear", executionCourse.getExecutionYear().getYear());
        wrap.put("executionPeriod", executionCourse.getExecutionPeriod().getName());
        wrap.put("url", executionCourse.getSite() != null ? executionCourse.getSite().getFullUrl() : "#");
        return wrap;
    }
}
