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

import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.ProfessorshipPermissions;
import org.fenixedu.academic.domain.accessControl.TeacherGroup;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.ViewPost;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.executionCourse.components.*;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.Atomic;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Joiner.on;
import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;
import static org.fenixedu.cms.domain.component.Component.register;

public class ExecutionCourseListener {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DegreeInfo.class);

    public static final String BUNDLE = "resources.FenixEduLearningResources";

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
    public static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduLearningResources", "label.menu");
    public static final LocalizedString EXTRA_MENU_TITLE = getLocalizedString("resources.FenixEduLearningResources", "label.extra.menu");

    public static Site create(ExecutionCourse executionCourse) {
        final Site newSite = new Site(executionCourse.getNameI18N().toLocalizedString(),
                getObjectives(executionCourse).orElseGet(() -> executionCourse.getNameI18N().toLocalizedString()));
        executionCourse.setSite(newSite);
        newSite.setSlug(formatSlugForExecutionCourse(executionCourse));
        newSite.setTheme(CMSTheme.forType("fenixedu-learning-theme"));

        final Menu menu = createSiteMenu(executionCourse, newSite);
        createDefaultContents(newSite, menu, Authenticate.getUser());
        createSiteRoles(executionCourse, newSite);

        MenuFunctionality functionality = MenuFunctionality.findFunctionality("cms", "disciplinas");
        if (functionality == null || functionality.getCmsFolder() == null){
            throw new DomainException("site.folder.not.found");
        }
        newSite.setFolder(functionality.getCmsFolder());
        newSite.setPublished(true);

        logger.info("Created site for execution course " + executionCourse.getSigla());
        return newSite;
    }

    private static Menu createSiteMenu(ExecutionCourse executionCourse, Site newSite) {
        final Menu menu = new Menu(newSite, executionCourse.getNameI18N().toLocalizedString());
        menu.setName(MENU_TITLE);
        menu.setPrivileged(true);
        menu.setOrder(0);

        final Menu extraPages = new Menu(newSite, executionCourse.getNameI18N().toLocalizedString());
        extraPages.setName(EXTRA_MENU_TITLE);
        extraPages.setOrder(1);
        return menu;
    }

    private static void createSiteRoles(ExecutionCourse executionCourse, Site newSite) {
        new Role(DefaultRoles.getInstance().getAuthorRole(), newSite);
        new Role(DefaultRoles.getInstance().getContributorRole(), newSite);
        new Role(DefaultRoles.getInstance().getEditorRole(), newSite);

        Role teacherRole = new Role(DefaultTeacherRole.getInstance().getTeacherRole(), newSite);
        Group group = Group.users(executionCourse.getProfessorshipsSet().stream().filter(p -> p.getPermissions().getSections())
                .map(p -> p.getPerson().getUser()));

        teacherRole.setGroup(group.toPersistentGroup());
    }

    private static String formatSlugForExecutionCourse(ExecutionCourse executionCourse) {
        return on("-").join(executionCourse.getSigla(), executionCourse.getExternalId());
    }

    private static Optional<LocalizedString> getObjectives(ExecutionCourse executionCourse) {
        return executionCourse.getCompetenceCourses().stream()
                .map(competenceCourse -> competenceCourse.getObjectivesI18N(executionCourse.getExecutionPeriod()))
                .filter(Objects::nonNull).map(MultiLanguageString::toLocalizedString).findFirst();
    }
    public static void createDefaultContents(Site site, Menu menu, User author) {

        Category summariesCategory = site.getOrCreateCategoryForSlug("summary", SUMMARIES_TITLE);
        Category announcementsCategory = site.getOrCreateCategoryForSlug("announcement", ANNOUNCEMENTS_TITLE);

        ListCategoryPosts summariesComponent = new ListCategoryPosts(summariesCategory);
        ListCategoryPosts announcementsComponent = new ListCategoryPosts(announcementsCategory);

        Component referencesComponent = forType(BibliographicReferencesComponent.class);
        Component evaluationMethodsComponent = forType(EvaluationMethodsComponent.class);
        // TODO: recreate this somehow
        //Component inquiriesResultsComponent = forType(InquiriesResultsComponent.class);
        Component homeComponent = forType(InitialPageComponent.class);

        Page initialPage = Page.create(site, menu, null, INITIAL_PAGE_TITLE, true, "firstPage", author, homeComponent,
                announcementsComponent);
        Page.create(site, menu, null, GROUPS_TITLE, true, "groupings", author, forType(GroupsComponent.class));
        Page.create(site, menu, null, EVALUATIONS_TITLE, true, "evaluations", author, forType(EvaluationsComponent.class));
        Page.create(site, menu, null, REFERENCES_TITLE, true, "bibliographicReferences", author, referencesComponent);
        Page.create(site, menu, null, SCHEDULE_TITLE, true, "calendarEvents", author, forType(ScheduleComponent.class));
        Page.create(site, menu, null, EVALUATION_METHOD_TITLE, true, "evaluationMethods", author, evaluationMethodsComponent);
        Page.create(site, menu, null, OBJECTIVES_TITLE, true, "objectives", author, forType(ObjectivesComponent.class));
        Page.create(site, menu, null, LESSON_PLAN_TITLE, true, "lessonPlan", author, forType(LessonPlanComponent.class));
        Page.create(site, menu, null, PROGRAM_TITLE, true, "program", author, forType(ObjectivesComponent.class));
        //Page.create(newSite, menu, null, INQUIRIES_RESULTS_TITLE, true, "inqueriesResults", user, inquiriesResultsComponent, menuComponent);
        Page.create(site, menu, null, SHIFTS_TITLE, true, "shifts", author, forType(ExecutionCourseComponent.class));
        Page.create(site, menu, null, ANNOUNCEMENTS_TITLE, true, "category", author, announcementsComponent);
        Page.create(site, menu, null, SUMMARIES_TITLE, true, "category", author, summariesComponent);
        Page.create(site, menu, null, MARKS_TITLE, true, "marks", author, forType(MarksComponent.class));
        Page.create(site, null, null, VIEW_POST_TITLE, true, "view", author, forType(ViewPost.class));
        //TODO content search
        site.setInitialPage(initialPage);

    }

    public static void updateSiteSlug(ExecutionCourse instance) {
        instance.getSite().setSlug(formatSlugForExecutionCourse(instance));
        instance.setSiteUrl(instance.getSite().getFullUrl());
    }

    public static void updateProfessorship(Professorship professorship, Boolean allowAccess ) {
        ExecutionCourse executionCourse = professorship.getExecutionCourse();
        Role teacherRole = executionCourse.getSite().getRolesSet().stream()
                .filter(role->role.getRoleTemplate().equals(DefaultTeacherRole.getInstance().getTeacherRole()))
                .findAny().orElseGet(()->new Role(DefaultTeacherRole.getInstance().getTeacherRole(),executionCourse.getSite()));

        Group group = teacherRole.getGroup().toGroup();

        if(allowAccess){
            group=group.grant(professorship.getPerson().getUser());
        } else {
            group=group.revoke(professorship.getPerson().getUser());
        }

        teacherRole.setGroup(group.toPersistentGroup());

    }
}
