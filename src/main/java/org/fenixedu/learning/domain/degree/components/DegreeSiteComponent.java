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
