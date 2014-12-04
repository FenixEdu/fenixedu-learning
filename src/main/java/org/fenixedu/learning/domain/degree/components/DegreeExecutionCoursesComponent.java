package org.fenixedu.learning.domain.degree.components;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static net.sourceforge.fenixedu.dataTransferObject.ExecutionCourseView.COMPARATOR_BY_NAME;
import static net.sourceforge.fenixedu.domain.ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR;

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.Supplier;

import net.sourceforge.fenixedu.dataTransferObject.ExecutionCourseView;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.util.PeriodState;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Created by borgez on 10/8/14.
 */
@ComponentType(name = "Degree execution courses", description = "execution courses for a degree")
public class DegreeExecutionCoursesComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Degree degree = degree(page);
        globalContext.put("executionCoursesBySemesterAndCurricularYear", executionCourses(degree));
        globalContext.put("executionYears", degree.getDegreeCurricularPlansExecutionYears());
    }

    public SortedMap<ExecutionSemester, SortedMap<Integer, SortedSet<ExecutionCourseView>>> executionCourses(final Degree degree) {
        TreeMap<ExecutionSemester, SortedMap<Integer, SortedSet<ExecutionCourseView>>> result =
                Maps.newTreeMap(COMPARATOR_BY_SEMESTER_AND_YEAR);
        
        ExecutionSemester currentExecutionPeriod = ExecutionSemester.readActualExecutionSemester();
        ExecutionSemester previousExecutionPeriod = currentExecutionPeriod.getPreviousExecutionPeriod();
        ExecutionSemester nextExecutionSemester = currentExecutionPeriod.getNextExecutionPeriod();
        boolean hasNextExecutionSemester = nextExecutionSemester != null && nextExecutionSemester.getState().equals(PeriodState.OPEN);
        ExecutionSemester selectedExecutionPeriod = hasNextExecutionSemester ? nextExecutionSemester : previousExecutionPeriod;

        result.put(selectedExecutionPeriod, executionCourses(degree, selectedExecutionPeriod));
        result.put(currentExecutionPeriod, executionCourses(degree, currentExecutionPeriod));
        return result;
    }

    public SortedMap<Integer, SortedSet<ExecutionCourseView>> executionCourses(Degree degree, ExecutionSemester executionSemester) {
        Set<ExecutionCourseView> executionCoursesViews = Sets.newHashSet();
        degree.getActiveDegreeCurricularPlans().forEach(plan->plan.addExecutionCourses(executionCoursesViews, executionSemester));
        return executionCoursesViews.stream()
                .collect(groupingBy(ExecutionCourseView::getCurricularYear, TreeMap::new, toCollection(factory)));
    }

    private static final Supplier<SortedSet<ExecutionCourseView>> factory = ()->Sets.newTreeSet(COMPARATOR_BY_NAME);

}
