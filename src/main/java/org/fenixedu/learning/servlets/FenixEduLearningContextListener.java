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
package org.fenixedu.learning.servlets;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Summary;
import org.fenixedu.academic.domain.thesis.Thesis;
import org.fenixedu.academic.service.services.manager.MergeExecutionCourses;
import org.fenixedu.academic.service.services.teacher.PublishMarks;
import org.fenixedu.academic.service.services.teacher.PublishMarks.MarkPublishingBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseListener;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.learning.domain.executionCourse.SummaryListener;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;

@WebListener
public class FenixEduLearningContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Signal.register(Summary.CREATE_SIGNAL, (DomainObjectEvent<Summary> event) -> {
            Summary summary = event.getInstance();
            SummaryListener.updatePost(new Post(summary.getExecutionCourse().getSite()), summary);
        });
        FenixFramework.getDomainModel().registerDeletionListener(Summary.class, (summary) -> {
            Post post = summary.getPost();
            summary.setPost(null);
            post.delete();
        });
        Signal.register(Summary.EDIT_SIGNAL, (DomainObjectEvent<Summary> event) -> {
            SummaryListener.updatePost(event.getInstance().getPost(), event.getInstance());
        });

        Signal.register(ExecutionCourse.CREATED_SIGNAL, (DomainObjectEvent<ExecutionCourse> event) -> {
            ExecutionCourseListener.create(event.getInstance());
        });
        Signal.register(PublishMarks.MARKS_PUBLISHED_SIGNAL, FenixEduLearningContextListener::handleMarksPublishment);
        Signal.register(Thesis.PROPOSAL_APPROVED_SIGNAL, FenixEduLearningContextListener::handleThesisProposalApproval);
        FenixFramework.getDomainModel().registerDeletionListener(ExecutionCourse.class, (executionCourse) -> {
            if (executionCourse.getSite() != null) {
                executionCourse.getSite().delete();
            }
        });

        MergeExecutionCourses.registerMergeHandler(FenixEduLearningContextListener::copyExecutionCoursesSites);
    }

    private static void copyExecutionCoursesSites(ExecutionCourse from, ExecutionCourse to) {
        Menu newMenu = to.getSite().getMenusSet().stream().findAny().get();

        LocalizedString newPageName =
                new LocalizedString().with(Locale.getDefault(), from.getName() + "(" + from.getDegreePresentationString() + ")");

        MenuItem emptyPageParent = PagesAdminService.create(to.getSite(), null, newPageName, new LocalizedString()).get();

        emptyPageParent.getPage().setPublished(false);
        emptyPageParent.setTop(newMenu);

        for (Menu oldMenu : from.getSite().getMenusSet()) {
            oldMenu.getToplevelItemsSorted().forEach(
                    menuItem -> PagesAdminService.copyStaticPage(menuItem, to.getSite(), newMenu, emptyPageParent));
        }
    }

    private static void handleMarksPublishment(MarkPublishingBean bean) {
        if (!Strings.isNullOrEmpty(bean.getEvaluation().getPublishmentMessage()) && bean.getCourse().getSite() != null) {
            Category cat = bean.getCourse().getSite().categoryForSlug("announcement");
            if (cat != null) {
                Post post = new Post(bean.getCourse().getSite());
                post.addCategories(cat);
                post.setName(bean.getTitle() == null ? BundleUtil.getLocalizedString("resources.ApplicationResources",
                        "message.publishment") : new LocalizedString(I18N.getLocale(), bean.getTitle()));
                post.setBody(new LocalizedString(I18N.getLocale(), bean.getEvaluation().getPublishmentMessage()));
            }
        }
    }

    private static void handleThesisProposalApproval(DomainObjectEvent<Thesis> event) {
        Thesis thesis = event.getInstance();
        if (thesis.getProposedDiscussed() == null || thesis.getDegree().getSite() == null) {
            return;
        }

        Category cat = thesis.getDegree().getSite().categoryForSlug("announcement");

        if (cat != null) {
            Post post = new Post(cat.getSite());
            post.addCategories(cat);
            post.setLocation(new LocalizedString(I18N.getLocale(), thesis.getProposedPlace()));

            LocalizedString subject =
                    BundleUtil.getLocalizedString(Bundle.MESSAGING, "thesis.announcement.subject", thesis.getStudent()
                            .getPerson().getName());

            LocalizedString body =
                    BundleUtil.getLocalizedString(Bundle.MESSAGING, "thesis.announcement.body", thesis.getStudent().getPerson()
                            .getName(), getDate(thesis.getProposedDiscussed()), String.valueOf(hasPlace(thesis)),
                            thesis.getProposedPlace(), String.valueOf(hasTime(thesis.getProposedDiscussed())),
                            getTime(thesis.getProposedDiscussed()), thesis.getTitle().getContent());

            post.setName(subject);
            post.setBody(body);
        }

    }

    private static int hasPlace(Thesis thesis) {
        String place = thesis.getProposedPlace();
        return place == null || place.trim().length() == 0 ? 0 : 1;
    }

    private static String getTime(DateTime dateTime) {
        return String.format(I18N.getLocale(), "%tR", dateTime.toDate());
    }

    private static int hasTime(DateTime proposedDiscussed) {
        if (proposedDiscussed.getHourOfDay() == 0 && proposedDiscussed.getMinuteOfHour() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    private static String getDate(DateTime dateTime) {
        return String.format(I18N.getLocale(), "%1$td de %1$tB de %1$tY", dateTime.toDate());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

    private static class PagesAdminService {

        @Atomic(mode = Atomic.TxMode.WRITE)
        protected static Optional<MenuItem> create(Site site, MenuItem parent, LocalizedString name, LocalizedString body) {
            Menu menu = site.getMenusSet().stream().findFirst().orElse(null);
            Page page = Page.create(site, menu, parent, Post.sanitize(name), true, "view", Authenticate.getUser());
            Category category =
                    site.getOrCreateCategoryForSlug("content", new LocalizedString().with(I18N.getLocale(), "Content"));
            Post post = Post.create(site, page, Post.sanitize(name), Post.sanitize(body), category, true, Authenticate.getUser());
            page.addComponents(new StaticPost(post));
            MenuItem menuItem = page.getMenuItemsSet().stream().findFirst().get();
            if (parent != null) {
                parent.add(menuItem);
            } else {
                menu.add(menuItem);
            }
            return Optional.of(menuItem);
        }

        protected static void copyStaticPage(MenuItem oldMenuItem, ExecutionCourseSite newSite, Menu newMenu, MenuItem newParent) {
            if (oldMenuItem.getPage() != null) {
                Page oldPage = oldMenuItem.getPage();
                staticPost(oldPage).ifPresent(oldPost -> {
                    Page newPage = new Page(newSite);
                    newPage.setName(oldPage.getName());
                    newPage.setTemplate(newSite.getTheme().templateForType(oldPage.getTemplate().getType()));
                    newPage.setCreatedBy(Authenticate.getUser());
                    newPage.setPublished(false);

                    for (Component component : oldPage.getComponentsSet()) {
                        if (component instanceof StaticPost) {
                            StaticPost staticPostComponent = (StaticPost) component;
                            Post newPost = clonePost(staticPostComponent.getPost(), newSite);
                            newPost.setActive(true);
                            StaticPost newComponent = new StaticPost(newPost);
                            newPage.addComponents(newComponent);
                        }
                    }

                    MenuItem newMenuItem = MenuItem.create(newMenu, newPage, oldMenuItem.getName(), newParent);
                    newMenuItem.setPosition(oldMenuItem.getPosition());
                    newMenuItem.setUrl(oldMenuItem.getUrl());
                    newMenuItem.setFolder(oldMenuItem.getFolder());

                    oldMenuItem.getChildrenSet().stream().forEach(child -> copyStaticPage(child, newSite, newMenu, newMenuItem));
                });
            }
        }

        private static Post clonePost(Post oldPost, Site newSite) {
            Post newPost = new Post(newSite);
            newPost.setName(oldPost.getName());
            newPost.setBody(oldPost.getBody());
            newPost.setCreationDate(new DateTime());
            newPost.setCreatedBy(Authenticate.getUser());
            newPost.setActive(oldPost.getActive());

            for (Category oldCategory : oldPost.getCategoriesSet()) {
                Category newCategory = newSite.getOrCreateCategoryForSlug(oldCategory.getSlug(), oldCategory.getName());
                newPost.addCategories(newCategory);
            }

            for (int i = 0; i < oldPost.getAttachments().getFiles().size(); ++i) {
                GroupBasedFile file = oldPost.getAttachments().getFiles().get(i);
                GroupBasedFile attachmentCopy =
                        new GroupBasedFile(file.getDisplayName(), file.getFilename(), file.getContent(), AnyoneGroup.get());
                newPost.getAttachments().putFile(attachmentCopy, i);
            }

            for (GroupBasedFile file : oldPost.getPostFiles().getFiles()) {
                GroupBasedFile postFileCopy =
                        new GroupBasedFile(file.getDisplayName(), file.getFilename(), file.getContent(), AnyoneGroup.get());
                newPost.getPostFiles().putFile(postFileCopy);
            }
            return newPost;
        }

        private static Optional<Post> staticPost(Page page) {
            return page.getComponentsSet().stream().filter(StaticPost.class::isInstance).map(StaticPost.class::cast)
                    .map(StaticPost::getPost).findFirst();
        }

    }
}
