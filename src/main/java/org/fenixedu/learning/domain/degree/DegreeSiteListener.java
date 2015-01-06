package org.fenixedu.learning.domain.degree;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
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

    public static DegreeSite create(Degree degree) {
        DegreeSite newSite = new DegreeSite(degree);
        newSite.setName(degree.getNameI18N().toLocalizedString());
        Menu menu = new Menu(newSite);
        menu.setName(MENU_TITLE);

        newSite.setTheme(CMSTheme.forType("fenixedu-learning-theme"));

        createDefaultContents(newSite, menu, Authenticate.getUser());

        return newSite;
    }

    public static void createDefaultContents(Site newSite, Menu menu, User user) {
        Component announcementsComponent =
                new ListCategoryPosts(newSite.getOrCreateCategoryForSlug("announcement", ANNOUNCEMENTS_TITLE));

        Page initialPage =
                Page.create(newSite, menu, null, DESCRIPTION_TITLE, true, "degreeDescription", user,
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
