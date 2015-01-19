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

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.degreeStructure.BibliographicReferences.BibliographicReference;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseInformation;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

import com.google.common.collect.Lists;

@ComponentType(name = "bibliographicReferences", description = "Bibliographic References for an Execution Course")
public class BibliographicReferencesComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("executionCourse", executionCourse);
        globalContext.put("mainReferences", mainReferences(executionCourse));
        globalContext.put("secondaryReferences", secundaryReferences(executionCourse));
        globalContext.put("optionalReferences", optionalReferences(executionCourse));
        globalContext.put("nonOptionalReferences", nonOptionalReferences(executionCourse));
    }

    public List<BibliographicReference> secundaryReferences(ExecutionCourse executionCourse) {
        return bibliographiReferences(executionCourse).stream().filter(b -> b.isSecondary()).collect(Collectors.toList());
    }

    public List<BibliographicReference> mainReferences(ExecutionCourse executionCourse) {
        return bibliographiReferences(executionCourse).stream().filter(b -> b.isMain()).collect(Collectors.toList());
    }

    public List<org.fenixedu.academic.domain.BibliographicReference> optionalReferences(ExecutionCourse executionCourse) {
        return executionCourse.getOrderedBibliographicReferences().stream().filter(b -> b.isOptional())
                .collect(Collectors.toList());
    }

    public List<org.fenixedu.academic.domain.BibliographicReference> nonOptionalReferences(ExecutionCourse executionCourse) {
        return executionCourse.getOrderedBibliographicReferences().stream().filter(b -> !b.isOptional())
                .collect(Collectors.toList());
    }

    public List<BibliographicReference> bibliographiReferences(ExecutionCourse executionCourse) {
        final List<BibliographicReference> references = Lists.newArrayList();
        for (CompetenceCourseInformation competenceCourseInfo : executionCourse.getCompetenceCoursesInformations()) {
            if (competenceCourseInfo.getBibliographicReferences() != null) {
                references.addAll(competenceCourseInfo.getBibliographicReferences().getBibliographicReferencesSortedByOrder());
            }
        }
        return references;
    }

}
