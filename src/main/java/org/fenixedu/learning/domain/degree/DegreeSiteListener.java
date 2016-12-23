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
package org.fenixedu.learning.domain.degree;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.cms.domain.Role;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;

import java.util.Locale;
import java.util.Optional;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

/**
 * Created by borgez on 24-11-2014.
 */
public class DegreeSiteListener {
    private static final String BUNDLE = "resources.FenixEduLearningResources";
    private static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduLearningResources", "label.menu");
    private static final LocalizedString ANNOUNCEMENTS_TITLE = getLocalizedString(BUNDLE, "label.announcements");
    private static final LocalizedString VIEW_POST_TITLE = getLocalizedString(BUNDLE, "label.viewPost");
    private static final LocalizedString TITLE_CURRICULUM = getLocalizedString(BUNDLE, "degree.curriculum.title");
    private static final LocalizedString DESCRIPTION_TITLE = getLocalizedString(BUNDLE, "degree.description.title");
    private static final LocalizedString REQUIREMENTS_TITLE = getLocalizedString(BUNDLE, "degree.requirements.title");
    private static final LocalizedString PROFESSIONAL_STATUS_TITLE = getLocalizedString(BUNDLE, "degree.pstatus.title");
    private static final LocalizedString CURRICULAR_PLAN_TITLE = getLocalizedString(BUNDLE, "degree.cplan.title");
    private static final LocalizedString EXECUTION_COURSE_SITES_TITLE = getLocalizedString(BUNDLE, "degree.ecsites.title");
    private static final LocalizedString TITLE_CLASS = getLocalizedString(BUNDLE, "degree.class.title");
    private static final LocalizedString CLASSES_TITLE = getLocalizedString(BUNDLE, "degree.classes.title");
    private static final LocalizedString EVALUATIONS_TITLE = getLocalizedString(BUNDLE, "degree.evaluations.title");
    private static final LocalizedString THESES_TITLE = getLocalizedString(BUNDLE, "degree.theses.title");
    private static final LocalizedString TITLE_THESIS = getLocalizedString(BUNDLE, "department.thesis");
    private static final LocalizedString TITLE_COURSE = getLocalizedString(BUNDLE, "department.course");
    private static final LocalizedString TITLE_CURRICULAR_COURSE = getLocalizedString(BUNDLE, "degree.curricularCourse.title");

    @Atomic
    public static Site create(Degree degree) {
        LocalizedString name;
    
        if (degree.getPhdProgram() != null) {
            name = new LocalizedString().with(Locale.getDefault(), degree.getPhdProgram().getPresentationName());
        } else {
            name = new LocalizedString().with(Locale.getDefault(), degree.getPresentationName());
        }
    
        Site site = DegreeSiteBuilder.getInstance().create(name);
    
        RoleTemplate defaultTemplate = site.getDefaultRoleTemplate();
        if (defaultTemplate != null ) {
            Role role = new Role(defaultTemplate, site);
            
            role.setGroup(degree.getCoordinatorGroupSet().stream()
                    .filter(pg -> pg.getResponsible()).map(pg -> pg.toGroup()).findAny().orElseGet(() -> Group.managers()));
        } else {
            throw new DomainException("no.default.role");
        }
        return site;
    }


    
    
}
