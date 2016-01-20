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

import com.google.common.collect.Lists;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.accessControl.StudentGroup;
import org.fenixedu.academic.domain.accessControl.StudentSharingDegreeOfCompetenceOfExecutionCourseGroup;
import org.fenixedu.academic.domain.accessControl.StudentSharingDegreeOfExecutionCourseGroup;
import org.fenixedu.academic.domain.accessControl.TeacherGroup;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.cms.domain.CMSFolder;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.cms.routing.CMSRenderer;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkNotNull;

public class ExecutionCourseRequestHandler implements CMSRenderer.RenderingPageHandler {
    @Override
    public void accept(Page page, TemplateContext templateContext) {
        if(page.getSite().getExecutionCourse() !=null ){
            templateContext.put("siteObject",page.getSite().getExecutionCourse());
        }
    }
}
