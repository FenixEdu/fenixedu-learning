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

import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by borgez on 10/9/14.
 */
@ComponentType(name = "Class Schedule", description = "Info about the class schedule of a degree")
public class ClassScheduleComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        SchoolClass schoolClass = getDomainObject(globalContext.getRequestContext()[1]);
        globalContext.put("defaultView", "agendaWeek");
        globalContext.put("eventsUrl", CoreConfiguration.getConfiguration().applicationUrl()
                + "/api/fenixedu-learning/events/degree/class/" + schoolClass.getExternalId());
        globalContext.put("dayToShow", ISODateTimeFormat.date().print(LocalDate.now()));
    }

}
