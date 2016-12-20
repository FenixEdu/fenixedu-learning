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

import org.fenixedu.academic.domain.Summary;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

public class SummaryListener {

    public static final String BUNDLE = "resources.FenixEduLearningResources";
    public static final LocalizedString SUMMARIES_TITLE = getLocalizedString(BUNDLE, "label.summaries");
    public static final String SUMMARIES_CATEGORY = "summary";

    public static void updatePost(Post post, Summary summary) {
		summary.setPost(post);
		if (post != null) {
			Site site = summary.getExecutionCourse().getSite();
			post.setSlug("summary-" + summary.getExternalId());
			post.setName(summary.getTitle().toLocalizedString());

			post.setBody(summary.getSummaryText().toLocalizedString());
			post.setCreationDate(summary.getSummaryDateTime());
			post.setActive(true);
			post.addCategories(site.getOrCreateCategoryForSlug(SUMMARIES_CATEGORY, SUMMARIES_TITLE));
		}
		Signal.emit(Post.SIGNAL_EDITED, new DomainObjectEvent<>(post));
    }

}
