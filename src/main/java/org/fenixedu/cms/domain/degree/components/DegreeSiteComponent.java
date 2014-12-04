package org.fenixedu.cms.domain.degree.components;

import net.sourceforge.fenixedu.domain.Degree;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.degree.DegreeSite;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;

public abstract class DegreeSiteComponent implements CMSComponent {

    protected Degree degree(Page page) {
        if (page.getSite() instanceof DegreeSite) {
            return ((DegreeSite) page.getSite()).getDegree();
        }
        throw new ResourceNotFoundException();
    }

}
