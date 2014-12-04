package org.fenixedu.cms.domain.executionCourse;

import net.sourceforge.fenixedu.domain.ExecutionCourse;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.MenuComponent;
import org.fenixedu.cms.domain.component.ViewPost;
import org.fenixedu.cms.domain.executionCourse.components.*;
import org.fenixedu.commons.i18n.LocalizedString;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

public class ExecutionCourseListener {
    public static final String BUNDLE = "resources.FenixEduCMSResources";

    public static final LocalizedString ANNOUNCEMENTS_TITLE = getLocalizedString(BUNDLE, "label.announcements");
    public static final LocalizedString VIEW_POST_TITLE = getLocalizedString(BUNDLE, "label.viewPost");
    private static final LocalizedString INITIAL_PAGE_TITLE = getLocalizedString(BUNDLE, "label.initialPage");
    private static final LocalizedString GROUPS_TITLE = getLocalizedString(BUNDLE, "label.groups");
    private static final LocalizedString EVALUATIONS_TITLE = getLocalizedString(BUNDLE, "label.evaluations");
    private static final LocalizedString REFERENCES_TITLE = getLocalizedString(BUNDLE, "label.bibliographicReferences");
    private static final LocalizedString SCHEDULE_TITLE = getLocalizedString(BUNDLE, "label.schedule");
    private static final LocalizedString EVALUATION_METHOD_TITLE = getLocalizedString(BUNDLE, "label.evaluationMethods");
    private static final LocalizedString OBJECTIVES_TITLE = getLocalizedString(BUNDLE, "label.objectives");
    private static final LocalizedString MARKS_TITLE = getLocalizedString(BUNDLE, "label.marks");
    private static final LocalizedString LESSON_PLAN_TITLE = getLocalizedString(BUNDLE, "label.lessonsPlanings");
    private static final LocalizedString PROGRAM_TITLE = getLocalizedString(BUNDLE, "label.program");
    private static final LocalizedString INQUIRIES_RESULTS_TITLE = getLocalizedString(BUNDLE, "label.inquiriesResults");
    private static final LocalizedString SUMMARIES_TITLE = getLocalizedString(BUNDLE, "label.summaries");
    private static final LocalizedString SHIFTS_TITLE = getLocalizedString(BUNDLE, "label.shifts");
    public static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduCMSResources", "label.menu");

    public static ExecutionCourseSite create(ExecutionCourse executionCourse) {
        final ExecutionCourseSite newSite = new ExecutionCourseSite(executionCourse);
        executionCourse.setCmsSite(newSite);
        final Menu menu = new Menu(newSite, MENU_TITLE);
        final User user = Authenticate.getUser();

        newSite.setTheme(CMSTheme.forType("fenixedu-learning-theme"));

        Category summariesCategory = newSite.categoryForSlug("summary", ANNOUNCEMENTS_TITLE);
        Category announcementsCategory = newSite.categoryForSlug("announcement", ANNOUNCEMENTS_TITLE);

        ListCategoryPosts summariesComponent = new ListCategoryPosts(summariesCategory);
        ListCategoryPosts announcementsComponent = new ListCategoryPosts(announcementsCategory);

        Component referencesComponent = forType(BibliographicReferencesComponent.class);
        Component evaluationMethodsComponent = forType(EvaluationMethodsComponent.class);
        Component inquiriesResultsComponent = forType(InquiriesResultsComponent.class);
        Component homeComponent = forType(InitialPageComponent.class);
        Component menuComponent = new MenuComponent(menu);

        Page initialPage = Page.create(newSite, menu, null, INITIAL_PAGE_TITLE, true, "firstPage", user, homeComponent, announcementsComponent, menuComponent);
        Page.create(newSite, menu, null, GROUPS_TITLE, true, "groupings", user, forType(GroupsComponent.class), menuComponent);
        Page.create(newSite, menu, null, EVALUATIONS_TITLE, true, "evaluations", user, forType(EvaluationsComponent.class), menuComponent);
        Page.create(newSite, menu, null, REFERENCES_TITLE, true, "bibliographicReferences", user, referencesComponent, menuComponent);
        Page.create(newSite, menu, null, SCHEDULE_TITLE, true, "calendarEvents", user, forType(ScheduleComponent.class), menuComponent);
        Page.create(newSite, menu, null, EVALUATION_METHOD_TITLE, true, "evaluationMethods", user, evaluationMethodsComponent, menuComponent);
        Page.create(newSite, menu, null, OBJECTIVES_TITLE, true, "objectives", user, forType(ObjectivesComponent.class), menuComponent);
        Page.create(newSite, menu, null, LESSON_PLAN_TITLE, true, "lessonPlan", user, forType(LessonPlanComponent.class), menuComponent);
        Page.create(newSite, menu, null, PROGRAM_TITLE, true, "program", user, forType(ObjectivesComponent.class), menuComponent);
        Page.create(newSite, menu, null, INQUIRIES_RESULTS_TITLE, true, "inqueriesResults", user, inquiriesResultsComponent, menuComponent);
        Page.create(newSite, menu, null, SHIFTS_TITLE, true, "shifts", user, forType(ExecutionCourseComponent.class), menuComponent);
        Page.create(newSite, menu, null, ANNOUNCEMENTS_TITLE, true, "category", user, announcementsComponent, menuComponent);
        Page.create(newSite, menu, null, SUMMARIES_TITLE, true, "category", user, summariesComponent, menuComponent);
        Page.create(newSite, menu, null, MARKS_TITLE, true, "marks", user, forType(MarksComponent.class), menuComponent);
        Page.create(newSite, null, null, VIEW_POST_TITLE, true, "view", user, forType(ViewPost.class), menuComponent);
        //TODO content search
        newSite.setInitialPage(initialPage);

        return newSite;
    }
}
