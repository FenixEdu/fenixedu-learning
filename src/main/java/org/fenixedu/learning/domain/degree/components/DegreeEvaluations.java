package org.fenixedu.learning.domain.degree.components;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by borgez on 10/14/14.
 */
@ComponentType(name = "Degree Evaluations", description = "Evaluations for a degree")
public class DegreeEvaluations extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Degree degree = degree(page);
        globalContext.put("defaultView", "month");
        globalContext.put("eventsUrl", CoreConfiguration.getConfiguration().applicationUrl()
                + "/api/fenixedu-learning/events/degree/evaluations/" + degree.getExternalId());
        globalContext.put("dayToShow", ISODateTimeFormat.date().print(LocalDate.now()));
    }

}
