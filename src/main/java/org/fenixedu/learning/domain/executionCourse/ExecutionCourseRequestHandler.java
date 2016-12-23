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

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.cms.routing.CMSRenderer;

public class ExecutionCourseRequestHandler implements CMSRenderer.RenderingPageHandler {
    @Override
    public void accept(Page page, TemplateContext templateContext) {
        if(page.getSite().getExecutionCourse() !=null ){
            templateContext.put("siteObject",page.getSite().getExecutionCourse());
        }
    }
}
