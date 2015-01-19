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
