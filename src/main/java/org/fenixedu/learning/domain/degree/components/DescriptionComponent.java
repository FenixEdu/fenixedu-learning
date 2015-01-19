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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.*;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.spaces.domain.Space;

import pt.ist.fenixframework.FenixFramework;

@ComponentType(name = "degreeDescription", description = "Description of a Degree")
public class DescriptionComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext global) {
        Degree degree = degree(page);

        global.put("degreeName",
                degree.getPhdProgram() != null ? degree.getPhdProgram().getPresentationName() : degree.getPresentationName());

        ExecutionYear targetExecutionYear = getTargetExecutionYear(global, degree);
        global.put("year", targetExecutionYear.getYear());

        Collection<Space> campi = degree.getCampus(targetExecutionYear);
        if (campi.isEmpty()) {
            campi = degree.getCurrentCampus();
        }
        global.put("campi", campi.stream().map(campus -> campus.getName()).collect(Collectors.toList()));

        Collection<Teacher> responsibleCoordinatorsTeachers = degree.getResponsibleCoordinatorsTeachers(targetExecutionYear);
        if (responsibleCoordinatorsTeachers.isEmpty()) {
            responsibleCoordinatorsTeachers = degree.getCurrentResponsibleCoordinatorsTeachers();
        }
        global.put("coordinators", responsibleCoordinatorsTeachers);

        DegreeInfo degreeInfo = targetExecutionYear.getDegreeInfo(degree);
        if (degreeInfo == null) {
            degreeInfo = degree.getMostRecentDegreeInfo(targetExecutionYear.getAcademicInterval());
        }
        global.put("degreeInfo", degreeInfo);

    }

    private ExecutionYear getTargetExecutionYear(TemplateContext global, Degree degree) {
        String executionDegreeId = global.getParameter("executionDegreeID");
        if (executionDegreeId != null) {

            ExecutionDegree executionDegree = FenixFramework.getDomainObject(executionDegreeId);
            if (executionDegree == null || !executionDegree.getDegreeCurricularPlan().getDegree().equals(degree)) {
                throw new RuntimeException("Unknown Execution Degree identifier.");
            }

            return executionDegree.getExecutionYear();
        } else {
            final ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();

            List<ExecutionYear> whenDegreeIsExecuted = degree.getDegreeCurricularPlansExecutionYears();
            if (whenDegreeIsExecuted.isEmpty()) {
                return currentExecutionYear;
            } else {
                final ExecutionYear firstExecutionYear = whenDegreeIsExecuted.iterator().next();
                final ExecutionYear lastExecutionYear = whenDegreeIsExecuted.get(whenDegreeIsExecuted.size() - 1);

                if (whenDegreeIsExecuted.contains(currentExecutionYear)) {
                    return currentExecutionYear;
                } else {
                    if (currentExecutionYear.isBefore(firstExecutionYear)) {
                        return firstExecutionYear;
                    } else {
                        return lastExecutionYear;
                    }
                }
            }
        }
    }
}
