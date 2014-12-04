package org.fenixedu.cms.domain.executionCourse.components;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.Curriculum;
import net.sourceforge.fenixedu.domain.ExecutionCourse;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.executionCourse.CompetenceCourseBean;
import org.fenixedu.cms.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "CompetenceCourse", description = "Competence Course information for an Execution Course")
public class ObjectivesComponent implements CMSComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("executionPeriod", executionCourse.getExecutionPeriod());
        globalContext.put("competenceCourseBeans", CompetenceCourseBean.approvedCompetenceCourses(executionCourse));
        globalContext.put("curriculumByCurricularCourse", curriculumsByCurricularCourses(executionCourse));
    }

    private Map<CurricularCourse, Curriculum> curriculumsByCurricularCourses(ExecutionCourse executionCourse) {
        Date end = executionCourse.getExecutionPeriod().getExecutionYear().getEndDate();
        return executionCourse
                .getCurricularCoursesSortedByDegreeAndCurricularCourseName()
                .stream()
                .filter(curricularCourse -> !curricularCourse.isBolonhaDegree())
                .collect(
                        Collectors.toMap(Function.identity(),
                                curricularCourse -> curricularCourse.findLatestCurriculumModifiedBefore(end)));
    }

}
