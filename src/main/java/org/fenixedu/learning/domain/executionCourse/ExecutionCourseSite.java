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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.cms.domain.CMSFolder;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;

import com.google.common.collect.Lists;

public class ExecutionCourseSite extends ExecutionCourseSite_Base {

    public ExecutionCourseSite(ExecutionCourse executionCourse) {
        checkNotNull(executionCourse);
        setExecutionCourse(executionCourse);
        setPublished(true);
        setFolder(folderForPath(PortalConfiguration.getInstance().getMenu(), "courses"));
        setSlug(formatSlug(getExecutionCourse()));
        setCanAdminGroup(NobodyGroup.get());
        setCanPostGroup(NobodyGroup.get());
        setBennu(Bennu.getInstance());

        executionCourse.setSiteUrl(getFullUrl());
    }

    private String formatSlug(ExecutionCourse executionCourse) {
        return on("-").join(executionCourse.getSigla(), executionCourse.getExternalId());
    }

    @Override
    public LocalizedString getName() {
        return getExecutionCourse().getNameI18N().toLocalizedString();
    }

    @Override
    public LocalizedString getDescription() {
        return getObjectives().orElseGet(this::getName);
    }

    private Optional<LocalizedString> getObjectives() {
        return getExecutionCourse().getCompetenceCourses().stream()
                .map(competenceCourse -> competenceCourse.getObjectivesI18N(getExecutionCourse().getExecutionPeriod()))
                .filter(Objects::nonNull).map(MultiLanguageString::toLocalizedString).findFirst();
    }

    @Override
    @Atomic
    public void delete() {
        setExecutionCourse(null);
        super.delete();
    }

    private CMSFolder folderForPath(MenuContainer parent, String path) {

        LocalizedString.Builder description = new LocalizedString.Builder();
        CoreConfiguration
                .supportedLocales()
                .stream()
                .forEach(
                        l -> description.with(l,
                                BundleUtil.getString("resources.FenixEduLearningResources", l, "label.course.folder.description")));

        return parent.getOrderedChild().stream().filter(item -> item.getPath().equals(path))
                .map(item -> item.getAsMenuFunctionality().getCmsFolder()).findAny()
                .orElseGet(() -> new CMSFolder(parent, path, description.build()));
    }

    public List<Group> getContextualPermissionGroups() {
        List<Group> groups = Lists.newArrayList();
        groups.add(AnyoneGroup.get());
        groups.add(LoggedGroup.get());
        groups.add(TeacherGroup.get(getExecutionCourse()));
        groups.add(TeacherGroup.get(getExecutionCourse()).or(StudentGroup.get(getExecutionCourse())));
        groups.add(StudentSharingDegreeOfExecutionCourseGroup.get(getExecutionCourse()));
        groups.add(StudentSharingDegreeOfCompetenceOfExecutionCourseGroup.get(getExecutionCourse()));
        return groups;
    }

    @Override
    public DomainObject getObject() {
        return getExecutionCourse();
    }

    public Stream<Wrap> getCategoriesToShow() {
        return Stream.of(categoryForSlug("announcement"), categoryForSlug("summary")).filter(Objects::nonNull)
                .map(Category::makeWrap);
    }

    public void updateSiteUrl(ExecutionCourse executionCourse) {
        setSlug(formatSlug(executionCourse));
        executionCourse.setSiteUrl(getFullUrl());
    }
}
