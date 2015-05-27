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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.fenixedu.academic.domain.ExecutionYear.readCurrentExecutionYear;
import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.RegimeType;
import org.fenixedu.academic.util.CurricularPeriodLabelFormatter;
import org.fenixedu.academic.util.CurricularRuleLabelFormatter;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Strings;

/**
 * Created by borgez on 10/10/14.
 */
@ComponentType(name = "Degree Curriculum", description = "Curriculum for a degree")
public class DegreeCurriculumComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Degree degree = degree(page);
        String pageUrl = pageForComponent(page.getSite(), CurricularCourseComponent.class).map(Page::getAddress).orElse(null);
        ExecutionYear selectedYear = selectedYear((String) globalContext.get("year"), degree);
        globalContext.put("courseGroups", courseGroups(degree, selectedYear, pageUrl));
        globalContext.put("allCurricularCourses",
                allCurricularCourses(courseGroups(degree, selectedYear, pageUrl).collect(toSet())));

        globalContext.put("coursesByCurricularSemester", coursesByCurricularSemester(degree, selectedYear, pageUrl));
        globalContext.put("years", degree.getDegreeCurricularPlansExecutionYears());
        globalContext.put("selectedYear", selectedYear);
    }

    SortedMap<CurricularPeriod, Set<CurricularCourseWrap>> coursesByCurricularSemester(Degree degree, ExecutionYear year,
            String pageUrl) {
        return allCurricularCourses(courseGroups(degree, year, pageUrl).collect(toSet())).collect(
                groupingBy(CurricularCourseWrap::getCurricularPeriod, TreeMap::new, toCollection(TreeSet::new)));
    }

    Stream<CurricularCourseWrap> allCurricularCourses(Collection<CourseGroupWrap> fathers) {
        Stream<CurricularCourseWrap> childrenCall =
                fathers.stream().flatMap(father -> allCurricularCourses(father.getCourseGroups().collect(toSet())));
        return Stream.concat(fathers.stream().flatMap(CourseGroupWrap::getCurricularCourses), childrenCall);
    }

    Stream<CourseGroupWrap> courseGroups(Degree degree, ExecutionYear year, String pageUrl) {
        return degree.getDegreeCurricularPlansForYear(year).stream().filter(plan -> plan.isApproved() && plan.isActive())
                .map(plan -> new CourseGroupWrap(null, plan.getRoot(), year, pageUrl));
    }

    ExecutionYear selectedYear(String year, Degree degree) {
        if (!Strings.isNullOrEmpty(year)) {
            return getDomainObject(year);
        } else {
            if (degree.getLastActiveDegreeCurricularPlan() != null) {
                return degree.getLastActiveDegreeCurricularPlan().getLastExecutionYear();
            } else {
                return readCurrentExecutionYear();
            }
        }
    }

    private class CourseGroupWrap extends Wrap {

        private final ExecutionYear executionInterval;
        private final CourseGroup courseGroup;
        private final Context previous;
        private final String pageUrl;

        public CourseGroupWrap(Context previous, CourseGroup courseGroup, ExecutionYear executionInterval, String pageUrl) {
            this.executionInterval = executionInterval;
            this.courseGroup = courseGroup;
            this.previous = previous;
            this.pageUrl = pageUrl;
        }

        public boolean hasCurricularCourses() {
            return courseGroup.hasAnyChildContextWithCurricularCourse();
        }

        public LocalizedString getName() {
            return courseGroup.getNameI18N().toLocalizedString();
        }

        public Stream<String> getRules() {
            return courseGroup.getVisibleCurricularRules(executionInterval).stream()
                    .filter(rule -> rule.appliesToContext(previous)).map(rule -> CurricularRuleLabelFormatter.getLabel(rule));
        }

        public Stream<CurricularCourseWrap> getCurricularCourses() {
            return courseGroup.getSortedOpenChildContextsWithCurricularCourses(executionInterval).stream()
                    .map(context -> new CurricularCourseWrap(context, executionInterval, pageUrl));
        }

        public Stream<CourseGroupWrap> getCourseGroups() {
            return courseGroup
                    .getSortedOpenChildContextsWithCourseGroups(executionInterval)
                    .stream()
                    .map(context -> new CourseGroupWrap(context, (CourseGroup) context.getChildDegreeModule(), executionInterval,
                            pageUrl));
        }
    }

    private class CurricularCourseWrap extends Wrap implements Comparable<CurricularCourseWrap> {
        private final Context context;
        private final ExecutionYear executionInterval;
        private final CurricularCourse curricularCourse;
        private final String pageUrl;

        public CurricularCourseWrap(Context context, ExecutionYear executionInterval, String pageUrl) {
            this.context = context;
            this.pageUrl = pageUrl;
            this.curricularCourse = (CurricularCourse) context.getChildDegreeModule();
            this.executionInterval = executionInterval;
        }

        public boolean isOptional() {
            return curricularCourse.isOptional();
        }

        public LocalizedString getName() {
            MultiLanguageString mls = curricularCourse.getNameI18N(executionInterval);
            return mls.isEmpty() ? new LocalizedString(I18N.getLocale(), "-") : mls.toLocalizedString();
        }

        public String getUrl() {
            return pageUrl + "/" + curricularCourse.getExternalId();
        }

        public String getContextInformation() {
            return CurricularPeriodLabelFormatter.getFullLabel(context.getCurricularPeriod(), true);
        }

        public boolean isSemestrial() {
            return curricularCourse.isSemestrial(executionInterval);
        }

        public boolean hasRegime() {
            return !isOptional() && curricularCourse.hasRegime(executionInterval);
        }

        public String getRegime() {
            RegimeType regime = curricularCourse.getRegime(executionInterval);
            return regime == null ? "-" : regime.getLocalizedName();
        }

        public String getRegimeAcronym() {
            RegimeType regime = curricularCourse.getRegime(executionInterval);
            return regime == null ? "-" : regime.getAcronym();
        }

        public String getContactLoad() {
            Double load = curricularCourse.getContactLoad(null, executionInterval);
            return new BigDecimal(load).setScale(2, RoundingMode.HALF_EVEN).toPlainString();
        }

        public String getAutonomousWorkHours() {
            return curricularCourse.getAutonomousWorkHours(context.getCurricularPeriod(), executionInterval).toString();
        }

        public String getTotalLoad() {
            return curricularCourse.getTotalLoad(null, executionInterval).toString();
        }

        public String getECTS() {
            return curricularCourse.getEctsCredits((CurricularPeriod) null, executionInterval).toString();
        }

        public boolean hasCompentenceCourse() {
            return curricularCourse.getCompetenceCourse() != null;
        }

        public Stream<String> getRules() {
            return curricularCourse.getVisibleCurricularRules(executionInterval).stream()
                    .filter(rule -> rule.appliesToContext(context)).map(rule -> CurricularRuleLabelFormatter.getLabel(rule));
        }

        public CurricularPeriod getCurricularPeriod() {
            return context.getCurricularPeriod();
        }

        @Override
        public int compareTo(CurricularCourseWrap o) {
            return getName().compareTo(o.getName());
        }
    }

}
