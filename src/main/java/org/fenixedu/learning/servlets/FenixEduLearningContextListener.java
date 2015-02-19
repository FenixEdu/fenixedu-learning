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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Summary;
import org.fenixedu.academic.domain.thesis.Thesis;
import org.fenixedu.academic.service.services.teacher.PublishMarks;
import org.fenixedu.academic.service.services.teacher.PublishMarks.MarkPublishingBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseListener;
import org.fenixedu.learning.domain.executionCourse.SummaryListener;
import org.joda.time.DateTime;

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
            if(post!=null) {
                summary.setPost(null);
                post.delete();
            }
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
}
