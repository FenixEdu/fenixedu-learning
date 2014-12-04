package org.fenixedu.learning.domain.degree.components;

import com.google.common.collect.ImmutableSet;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.thesis.Thesis;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import static net.sourceforge.fenixedu.domain.Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID;
import static net.sourceforge.fenixedu.domain.ExecutionYear.COMPARATOR_BY_YEAR;
import static org.fenixedu.cms.domain.DissertationsUtils.allThesesByYear;
import static org.fenixedu.cms.domain.DissertationsUtils.getThesisStateMapping;

@ComponentType(name = "degreeDissertations", description = "Dissertations information for a Degree")
public class DegreeDissertationsComponent extends DegreeSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Collection<Degree> degrees = ImmutableSet.of(degree(page));
        SortedMap<ExecutionYear, List<Thesis>> allThesesByYear = allThesesByYear(degrees);

        globalContext.put("unit", degree(page).getUnit());
        globalContext.put("thesesByYear", allThesesByYear);
        globalContext.put("years", allThesesByYear.keySet().stream().sorted(COMPARATOR_BY_YEAR));
        globalContext.put("degrees", degrees.stream().sorted(COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID));
        globalContext.put("states", getThesisStateMapping());
    }

}
