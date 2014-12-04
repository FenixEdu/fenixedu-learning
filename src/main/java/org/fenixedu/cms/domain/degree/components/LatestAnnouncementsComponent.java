package org.fenixedu.cms.domain.degree.components;

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
