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
package org.fenixedu.learning.domain.degree.components;

import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import java.util.Comparator;
import java.util.stream.Collectors;

@ComponentType(name = "latestAnnouncements", description = "Latest Announcements of a Degree")
public class LatestAnnouncementsComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext global) {
        Category announcements =
                page.getSite().getCategoriesSet().stream().filter(c -> c.getSlug().equals("announcement")).findAny().orElse(null);
        if (announcements != null) {
            global.put("announcements", announcements.getPostsSet().stream().sorted(new Comparator<Post>() {
                @Override
                public int compare(Post a, Post b) {
                    return a.getModificationDate().compareTo(b.getModificationDate());
                }
            }).limit(3).map(p -> p.makeWrap()).collect(Collectors.toList()));
            global.put("announcementsPage", announcements.getAddress());
        }
    }

}
