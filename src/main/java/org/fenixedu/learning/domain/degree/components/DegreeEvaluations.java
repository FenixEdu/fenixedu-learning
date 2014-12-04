package org.fenixedu.learning.domain.degree.components;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Project;
import net.sourceforge.fenixedu.domain.WrittenEvaluation;
import net.sourceforge.fenixedu.util.EvaluationType;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.ScheduleEventBean;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;

/**
 * Created by borgez on 10/14/14.
 */
@ComponentType(name = "Degree Evaluations", description = "Evaluations for a degree")
public class DegreeEvaluations extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Degree degree = degree(page);
        globalContext.put("defaultView", "month");
        globalContext.put("events", allPublicEvaluations(degree));
    }

    private Collection<ScheduleEventBean> allPublicEvaluations(Degree degree) {
        Set<ScheduleEventBean> allEvaluations = Sets.newHashSet(writtenEvaluations(degree));
        allEvaluations.addAll(projects(degree));
        return allEvaluations;
    }

    private Collection<ScheduleEventBean> writtenEvaluations(Degree degree) {
        return allExecutionCourses(degree).flatMap(executionCourse -> executionCourse.getAssociatedWrittenEvaluations().stream())
                .filter(writtenEval -> writtenEval.getBeginningDateTime() != null).map(evaluation -> createEventBean(evaluation))
                .collect(Collectors.toSet());
    }

    private ScheduleEventBean createEventBean(WrittenEvaluation evaluation) {
        ExecutionCourse ec = evaluation.getAssociatedExecutionCoursesSet().stream().findFirst().get();
        return new ScheduleEventBean(ec.getPrettyAcronym(), evaluation.getEvaluationType().toString(), evaluation.getFullName(),
                evaluation.getBeginningDateTime(), evaluation.getEndDateTime(), null, null,
                colorForType(evaluation.getEvaluationType()), null, null);
    }

    private Collection<ScheduleEventBean> projects(Degree degree) {
        Set<ScheduleEventBean> projects = Sets.newHashSet();
        allExecutionCourses(degree).forEach(executionCourse->{
            for(Project project : executionCourse.getAssociatedProjects()) {
                projects.addAll(projectEvents(project, executionCourse));
            }
        });
        return projects;
    }

    private static Collection<ScheduleEventBean> projectEvents(Project project, ExecutionCourse executionCourse) {
        DateTime projectStart = project.getProjectBeginDateTime();
        DateTime projectEnd = project.getProjectEndDateTime();

        ScheduleEventBean start =
                new ScheduleEventBean(executionCourse.getPrettyAcronym(), project.getEvaluationType().toString(),
                        project.getPresentationName(), projectStart, projectStart.plusHours(1), null, null,
                        colorForType(project.getEvaluationType()), null, null);

        ScheduleEventBean end =
                new ScheduleEventBean(executionCourse.getPrettyAcronym(), project.getEvaluationType().toString(),
                        project.getPresentationName(), projectEnd.minusHours(1), projectEnd, null, null,
                        colorForType(project.getEvaluationType()), null, null);

        return Sets.newHashSet(start, end);
    }

    private static String colorForType(EvaluationType type) {
        return ScheduleEventBean.COLORS[type.getType() % ScheduleEventBean.COLORS.length];
    }

    private Stream<ExecutionCourse> allExecutionCourses(Degree degree) {
        return degree.getDegreeCurricularPlansSet().stream()
                .flatMap(curricularPlan -> curricularPlan.getCurricularCoursesSet().stream())
                .flatMap(curricularCourse -> curricularCourse.getAssociatedExecutionCoursesSet().stream());
    }

    private static LocalizedString toLocalized(String string) {
        return new LocalizedString().with(I18N.getLocale(), string);
    }

}
