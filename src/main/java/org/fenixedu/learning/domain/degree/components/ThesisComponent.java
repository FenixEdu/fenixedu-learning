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

import org.fenixedu.academic.domain.thesis.Thesis;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.bennu.core.security.Authenticate;
import pt.ist.fenixframework.FenixFramework;

import static java.util.stream.Collectors.groupingBy;
import static org.fenixedu.learning.domain.DissertationsUtils.getThesisStateMapping;

@ComponentType(name = "thesis", description = "Provides information for a specific thesis")
public class ThesisComponent implements CMSComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Thesis thesis = FenixFramework.getDomainObject((String) globalContext.getRequestContext()[1]);
        globalContext.put("thesis", thesis);
        globalContext.put("states", getThesisStateMapping());
        globalContext.put("isAccessible", isAccessible(thesis));
    }

    private boolean isAccessible(Thesis thesis) {
        return thesis!=null && thesis.getDissertation()!=null && thesis.getDissertation().isAccessible(Authenticate.getUser());
    }

}