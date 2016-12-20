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

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.ViewPost;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.degree.components.ClassScheduleComponent;
import org.fenixedu.learning.domain.degree.components.CurricularCourseComponent;
import org.fenixedu.learning.domain.degree.components.DegreeClassesComponent;
import org.fenixedu.learning.domain.degree.components.DegreeCurricularPlansComponent;
import org.fenixedu.learning.domain.degree.components.DegreeCurriculumComponent;
import org.fenixedu.learning.domain.degree.components.DegreeDissertationsComponent;
import org.fenixedu.learning.domain.degree.components.DegreeEvaluations;
import org.fenixedu.learning.domain.degree.components.DegreeExecutionCoursesComponent;
import org.fenixedu.learning.domain.degree.components.DescriptionComponent;
import org.fenixedu.learning.domain.degree.components.LatestAnnouncementsComponent;
import org.fenixedu.learning.domain.degree.components.ThesisComponent;
import org.fenixedu.learning.domain.executionCourse.DefaultTeacherRole;
import pt.ist.fenixframework.Atomic;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
        Site newSite = new Site(getName(degree),getName(degree));
        newSite.setName(degree.getNameI18N().toLocalizedString());
        Menu menu = new Menu(newSite, degree.getNameI18N().toLocalizedString());
        menu.setName(MENU_TITLE);

        newSite.setTheme(CMSTheme.forType("fenixedu-learning-theme"));

        MenuFunctionality functionality = MenuFunctionality.findFunctionality("cms", "cursos");
        if (functionality == null || functionality.getCmsFolder() == null){
            throw new DomainException("site.folder.not.found");
        }
        newSite.setFolder(functionality.getCmsFolder());

        createDefaultContents(newSite, menu, Authenticate.getUser());
        createSiteRoles(degree,newSite);
        return newSite;
    }


    private static LocalizedString getName(Degree degree) {
        if (degree.getPhdProgram() != null) {
            return new LocalizedString().with(Locale.getDefault(), degree.getPhdProgram().getPresentationName());
        } else {
            return new LocalizedString().with(Locale.getDefault(), degree.getPresentationName());
        }
    }


    private static void createSiteRoles(Degree degree, Site newSite) {
        Role adminRole = new Role(DefaultRoles.getInstance().getAdminRole(), newSite);
        new Role(DefaultRoles.getInstance().getAuthorRole(), newSite);
        new Role(DefaultRoles.getInstance().getContributorRole(), newSite);
        new Role(DefaultRoles.getInstance().getEditorRole(), newSite);

        Group group = Group.users(degree.getCoordinatorGroupSet().stream().flatMap(PersistentGroup::getMembers).distinct());

        adminRole.setGroup(group.toPersistentGroup());
    }



    public static void createDefaultContents(Site newSite, Menu menu, User user) {
        Component announcementsComponent =
                new ListCategoryPosts(newSite.getOrCreateCategoryForSlug("announcement", ANNOUNCEMENTS_TITLE));

        Page initialPage = Page.create(newSite, menu, null, DESCRIPTION_TITLE, true, "degreeDescription", user,
                forType(DescriptionComponent.class), forType(LatestAnnouncementsComponent.class));
        Page.create(newSite, menu, null, ANNOUNCEMENTS_TITLE, true, "category", user, announcementsComponent);
        Page.create(newSite, menu, null, TITLE_CURRICULUM, true, "degreeCurriculum", user,
                forType(DegreeCurriculumComponent.class));

        Page.create(newSite, null, null, VIEW_POST_TITLE, true, "view", user, forType(ViewPost.class));
        Page.create(newSite, null, null, TITLE_THESIS, true, "dissertation", user, forType(ThesisComponent.class));
        Page.create(newSite, null, null, TITLE_CLASS, true, "calendarEvents", user, forType(ClassScheduleComponent.class));
        Page.create(newSite, null, null, TITLE_CURRICULAR_COURSE, true, "curricularCourse", user,
                forType(CurricularCourseComponent.class));

        Page.create(newSite, menu, null, THESES_TITLE, true, "dissertations", user, forType(DegreeDissertationsComponent.class));
        Page.create(newSite, menu, null, REQUIREMENTS_TITLE, true, "accessRequirements", user,
                forType(DescriptionComponent.class));
        Page.create(newSite, menu, null, PROFESSIONAL_STATUS_TITLE, true, "professionalStatus", user,
                forType(DescriptionComponent.class));
        Page.create(newSite, menu, null, CURRICULAR_PLAN_TITLE, true, "curricularPlans", user,
                forType(DegreeCurricularPlansComponent.class));
        Page.create(newSite, menu, null, EXECUTION_COURSE_SITES_TITLE, true, "degreeExecutionCourses", user,
                forType(DegreeExecutionCoursesComponent.class));
        Page.create(newSite, menu, null, EVALUATIONS_TITLE, true, "calendarEvents", user, forType(DegreeEvaluations.class));
        Page.create(newSite, menu, null, CLASSES_TITLE, true, "degreeClasses", user, forType(DegreeClassesComponent.class));

        newSite.setInitialPage(initialPage);

    }
}
