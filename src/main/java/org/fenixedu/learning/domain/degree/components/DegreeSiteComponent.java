package org.fenixedu.learning.domain.degree.components;

import java.util.Optional;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.learning.domain.degree.DegreeSite;

public abstract class DegreeSiteComponent implements CMSComponent {

    protected Degree degree(Page page) {
        if (page.getSite() instanceof DegreeSite) {
            return ((DegreeSite) page.getSite()).getDegree();
        }
        throw new ResourceNotFoundException();
    }

    public static Optional<Page> pageForComponent(Site site, Class<?> componentType) {
        for (Page page : site.getPagesSet()) {
            for (Component component : page.getComponentsSet()) {
                if (component.componentType() == componentType) {
                    return Optional.of(page);
                }
            }
        }
        return Optional.empty();
    }

    public static boolean supportsSite(Site site) {
        return site instanceof DegreeSite;
    }

}
