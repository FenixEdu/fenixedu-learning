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
package org.fenixedu.learning.domain.executionCourse;

import static com.google.common.base.Joiner.on;

import java.util.Objects;
import java.util.Optional;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.cms.domain.Role;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionCourseListener {
    

    private static final Logger logger = LoggerFactory.getLogger(ExecutionCourseListener.class);
    
    public static Site create(ExecutionCourse executionCourse) {
        if (executionCourse.getSite() == null) {
        Site newSite = ExecutionCourseSiteBuilder.getInstance().create(executionCourse);
        
        RoleTemplate defaultTemplate = newSite.getDefaultRoleTemplate();
        if (defaultTemplate != null ) {
            Role teacherRole =
            new Role(defaultTemplate, executionCourse.getSite());
    
            Group group = teacherRole.getGroup();
            for(Professorship pr : executionCourse.getProfessorshipsSet()) {
                User user = pr.getPerson().getUser();
                if (pr.getPermissions().getSections() && !group.isMember(user)) {
                    group = group.grant(user);
                } else if (group.isMember(user)) {
                    group = group.revoke(user);
                }
            }
                teacherRole.setGroup(group);
            } else {
                throw new DomainException("no.default.role");
            }
            
            logger.info("Created site for execution course " + executionCourse.getSigla());
            return newSite;
        } else {
            executionCourse.getSite().setName(executionCourse.getNameI18N());
            return executionCourse.getSite();
        }

    }

    public static void updateSiteSlug(ExecutionCourse instance) {
        instance.getSite().setSlug(ExecutionCourseSiteBuilder.formatSlugForExecutionCourse(instance));
        instance.setSiteUrl(instance.getSite().getFullUrl());
    }

    public static void updateProfessorship(Professorship professorship, Boolean allowAccess ) {
        ExecutionCourse executionCourse = professorship.getExecutionCourse();
    
        RoleTemplate defaultTemplate = executionCourse.getSite().getDefaultRoleTemplate();
        if (defaultTemplate != null ) {
            Role teacherRole = executionCourse.getSite().getRolesSet().stream()
                    .filter(role -> role.getRoleTemplate().equals(defaultTemplate))
                    .findAny().orElseGet(() -> new Role(defaultTemplate, executionCourse.getSite()));
        
            Group group = teacherRole.getGroup();
            
            User user = professorship.getPerson().getUser();
            if(allowAccess && !group.isMember(user)){
                group=group.grant(user);
                teacherRole.setGroup(group);
            } else if(group.isMember(user)) {
                group=group.revoke(user);
                teacherRole.setGroup(group);
            }
        } else {
            throw new DomainException("no.default.role");
        }
    
    }
}
