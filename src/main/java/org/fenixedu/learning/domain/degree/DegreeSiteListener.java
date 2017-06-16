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
package org.fenixedu.learning.domain.degree;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.cms.domain.Role;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;

import java.util.Locale;
import java.util.Optional;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

/**
 * Created by borgez on 24-11-2014.
 */
public class DegreeSiteListener {

    @Atomic
    public static Site create(Degree degree) {
        LocalizedString name;
    
        if (degree.getPhdProgram() != null) {
            name = new LocalizedString().with(Locale.getDefault(), degree.getPhdProgram().getPresentationName());
        } else {
            name = new LocalizedString().with(Locale.getDefault(), degree.getPresentationName());
        }
    
        Site site = DegreeSiteBuilder.getInstance().create(name);
    
        RoleTemplate defaultTemplate = site.getDefaultRoleTemplate();
        if (defaultTemplate != null ) {
            Group group = Group.users(degree.getCoordinatorGroupSet().stream().flatMap(PersistentGroup::getMembers).distinct());
            site.getDefaultRoleTemplateRole().setGroup(group);
        } else {
            throw new DomainException("no.default.role");
        }
        return site;
    }


    
    
}
