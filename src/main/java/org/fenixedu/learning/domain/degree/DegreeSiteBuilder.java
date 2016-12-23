package org.fenixedu.learning.domain.degree;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.ViewPost;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.degree.components.*;
import org.fenixedu.learning.domain.executionCourse.components.*;
import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

import java.util.Locale;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

/**
 * Created by diutsu on 20/01/17.
 */
public class DegreeSiteBuilder extends DegreeSiteBuilder_Base {
    
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

    private DegreeSiteBuilder(){
        super();
        this.setSlug(DegreeSiteBuilder.class.getSimpleName());
        Bennu.getInstance().getSiteBuildersSet().add(this);
     }
     
    public static DegreeSiteBuilder getInstance(){
        return Bennu.getInstance().getSiteBuildersSet().stream().filter(siteBuilder -> siteBuilder instanceof DegreeSiteBuilder)
                .map(siteBuilder -> (DegreeSiteBuilder) siteBuilder)
                .findFirst().orElseGet(()->new DegreeSiteBuilder());
    }
    
    public Site create(LocalizedString name) {
        
        Site site = super.create(name,name);
   
        final Menu menu = new Menu(site, site.getName());
        menu.setName(MENU_TITLE);
        menu.setPrivileged(true);
        menu.setOrder(0);
        
        site.setSystemMenu(menu);
        
        User user = Authenticate.getUser();

        Component announcementsComponent =
                new ListCategoryPosts(site.getOrCreateCategoryForSlug("announcement", ANNOUNCEMENTS_TITLE));
    
        Page initialPage = Page.create(site, menu, null, DESCRIPTION_TITLE, true, "degreeDescription", user,
                forType(DescriptionComponent.class), forType(LatestAnnouncementsComponent.class));
        Page.create(site, menu, null, ANNOUNCEMENTS_TITLE, true, "category", user, announcementsComponent);
        Page.create(site, menu, null, TITLE_CURRICULUM, true, "degreeCurriculum", user,
                forType(DegreeCurriculumComponent.class));
    
        Page.create(site, null, null, VIEW_POST_TITLE, true, "view", user, forType(ViewPost.class));
        Page.create(site, null, null, TITLE_THESIS, true, "dissertation", user, forType(ThesisComponent.class));
        Page.create(site, null, null, TITLE_CLASS, true, "calendarEvents", user, forType(ClassScheduleComponent.class));
        Page.create(site, null, null, TITLE_CURRICULAR_COURSE, true, "curricularCourse", user,
                forType(CurricularCourseComponent.class));
    
        Page.create(site, menu, null, THESES_TITLE, true, "dissertations", user, forType(DegreeDissertationsComponent.class));
        Page.create(site, menu, null, REQUIREMENTS_TITLE, true, "accessRequirements", user,
                forType(DescriptionComponent.class));
        Page.create(site, menu, null, PROFESSIONAL_STATUS_TITLE, true, "professionalStatus", user,
                forType(DescriptionComponent.class));
        Page.create(site, menu, null, CURRICULAR_PLAN_TITLE, true, "curricularPlans", user,
                forType(DegreeCurricularPlansComponent.class));
        Page.create(site, menu, null, EXECUTION_COURSE_SITES_TITLE, true, "degreeExecutionCourses", user,
                forType(DegreeExecutionCoursesComponent.class));
        Page.create(site, menu, null, EVALUATIONS_TITLE, true, "calendarEvents", user, forType(DegreeEvaluations.class));
        Page.create(site, menu, null, CLASSES_TITLE, true, "degreeClasses", user, forType(DegreeClassesComponent.class));
    
        site.setInitialPage(initialPage);
        
        return site;
    }
    
}


